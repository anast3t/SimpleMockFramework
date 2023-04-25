# Mocker
![java-v](https://img.shields.io/badge/java-11-orange?style=flat-square&logo=oracle)
![maintained](https://img.shields.io/maintenance/yes/2023?style=flat-square)
![vulnerabilities](https://img.shields.io/snyk/vulnerabilities/github/anast3t/SimpleMockFramework?style=flat-square)
![sponsors](https://img.shields.io/github/sponsors/anast3t?color=red&style=flat-square)

---
## Правила:
* У мокируемого объекта **_обязательно_** должен существовать пустой конструктор ("Empty constructor").
* У класса, передающегося в `any()` **_обязательно_** должен существовать пустой конструктор.
* От мокируемого интерфейса нельзя вызвать `thenImplemented()` если нет инстациированного объекта
* Все динамические и статические методы мокнутого объекта **_по умолчанию_** возвращают `null`
* Для работы со статиками **_необходим_** флаг JVM - `-Djdk.attach.allowAttachSelf=true`
---
## API:
`@Mock` - аннотация вешается на поля класса, которые необходимо мокнуть.
Поле может иметь инстанциированный объект, тогда он будет использоваться
при вызове имплементированных функций.

### `Mocker` - основной оперируемый класс.
* `.init(Object obj) -> void` - мокает все аннотированные поля в `obj`.
  * При возможности берет инициализированное поле для работы `.thenImplemented()`.
  * В ином случае инстанциирует объект по пустому конструктору.
* `.mock(...)`
  * `.mock(Class<T> mocking, Object instance) -> T` - создает мок класса `mocking`.
    * Имплементированные методы будут запускаться от `instance`.
    * `instance` может быть `null` только при условии, что `mocking` - интерфейс.
  * `.mock(T instance) -> T` - создаёт мок класса `T`.
    * Этим методом невозможно создать мок инерфейса.
  * `.mock(Class<T> mocking) -> T` - создаёт мок класса `mocking`.
    * Создаёт экземпляр класса автоматически по пустому конструктору.
    * Этим методом невозможно создать мок интерфейса.
* `.when(R smt) -> IMockRT<R>` - инициализирует создание действия на метод.
  * В аргумент передаётся вызов функции.
    * Пример: `Mocker.when(someClass.someMethod("someValue", 123))`.
* `.any(Class<T>) -> T` - позволяет указать `when`, что в данное поле может быть подставлено любое значение типа `T`
для поведения `then...()`.
  * Если `T` - пользовательский класс, то он должен иметь пустой конструктор.
  * **_НЕ РЕАЛИЗОВАНО_** для статических методов.
  * Количество N `any()` на 1 метод строго фиксировано - только одна вариация N `any()` на 1 метод.
    * Причина:
```java 
class Tests{
    //...
    @Test
    public void test(){
      //multiInput(String, Integer, Boolean)
      when(someClass.multiInput(any(String.class), 10, true)).thenReturn(1);
      when(someClass.multiInput("123", 10, any(Boolean.class))).thenReturn(2);

      // Если у нас есть 2 варинта поведения, то может получиться ситуация,
      // при которой невозможно сделать выбор из двух вариантов.
      
      Assertions.assertEquals(1, someClass.multiInput("123", 10, Boolean.TRUE));
      //Аналогичные ситуации могут произойти и при двух any()
      
      //По этой причине в текущей реализации каждая вариация поведения с некоторым количеством any()
      //перезатирает предыдущее поведение на то же количество any()
    }
    //...
}
```
### `IMockActions<R>` - интерфейс для создания действий на вызов метода.
Используется только в комбинации с `Mocker.when()`.
* `.thenNull() -> void` - функция будет возвращать `null`.
* `.thenReturn(R value) -> void` - функция будет возвращать `value`.
* `.thenThrow(Throwable exception) -> void` - функция будет выкидывать `exception`. 
* `.thenImplemented() -> void` - функция будет работать с поведением изначального класса.

---
## Иные ссылки

