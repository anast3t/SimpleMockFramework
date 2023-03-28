# Доклад

---

## Постановка задачи.

Mock фрэймворк - создаёт временные "заглушки" для тестирования объектов, с добавлением некоторого поведения.

Требования к реализации -

* `@Mock` - аннотация к объектам, которые необходимо мокнуть.
* `when` - "начало" определения поведения некоторой функции.
* `thenReturn` - определение поведения в виде возврата некоторого значения.
* `thenThrow` - определение поведения в виде выброса некоторой ошибки.

Необходимо было реализовать для динамических и статических методов классов и интерфейсов.

Реализовано - `@Mock`, `when`, `thenReturn`, `thenThrow`, `thenNull`, `thenImplemented`, `any`

---

## Правила:

* У мокируемого объекта **_обязательно_** должен существовать конструктор по умолчанию ("Empty constructor" или "Default
  constructor").
* У класса, передающегося в `any()` **_обязательно_** должен существовать конструктор по умолчанию,
  либо это классы обертки для примитивов (`Integer`, `Boolean` ...).
* От мокируемого интерфейса нельзя вызвать `thenImplemented()` если нет инстациированного объекта
* Все динамические и статические методы мокнутого объекта **_по умолчанию_** возвращают `null`
* Для работы со статиками **_необходим_** флаг JVM - `-Djdk.attach.allowAttachSelf=true`

## Ключевые моменты кода

Для реализации были использованы -

* **CGLib** - для создания прокси объектов и изменения поведения динамических методов
* **Javassist** - для изменения байткода статических методов
* **Reflection**
* **Instrumentation**

Для изменения статических методов используется **Java Agent**, который создаётся в рантайме и цепляется
к процессу JVM.

Для работы с мокнутыми объектами необходимо их проинициализировать.

### Инициализация

`Mocker.init(Object obj)` инициализирует все аннотированные через `@Mock` объекты внутри `obj`.
При возможности берет инициализированное поле для работы `.thenImplemented()`.
В ином случае инстанциирует объект по пустому конструктору.

* У объектов создаётся прокси объект, поведение динамических методов которого уже можно донастраивать далее.
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L48-L144
* На каждый прокси объект существует свой класс `MockCoreInstance` в котором уже и закладывается всё поведение.
  При инициализации в `Mocker` создаётся запись в `HashMap` вида **Прокси - Ядро**.
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/Mocker.java#L27
* Эта запись необходима при определении того, чьё ядро надо дергать для добавления поведения при `when()`.
  После каждого вызова метода из прокси объекта, в `Mocker` складывается информация о том, какой прокси объект вызывал
  какой метод (в поле `lastCalled`)
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/Mocker.java#L92-L100
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/Mocker.java#L83-L90
* У статических методов сразу при инициализации изменяется байткод, который добавляет перед основной реализации
  функции некоторый callback.
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockStaticCore.java#L50-L94
* В отличии от прокси объектов, тут нет привязки к сущностям, только к классам. Соответственно нет необходимости
  привязки обернутого объекта к некоторому "ядру". Для статических методов существует синглтон ядра, в котором уже 
  создаются записи в `HashMap` вида **((Класс, Метод, Параметры) -> (Возвращаемое значение, Тип действия))**
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockStaticCore.java#L25-L28
* У динамических методов существует подобная мапа, однако там она отдельная на каждое ядро прокси, поэтому вид у неё
  **((Метод, Параметры) -> (Возвращаемое значение, Тип действия))**
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L18-L21

### Изменение поведения

Теперь, после инициализации объектов с которыми мы хотим работать, можно изменять поведение их методов. 
Для создания некоторого поведения, необходимо применить конструкцию из `Mocker.when(...).then*(...)`.

* `Mocker.when(...)` - внутрь `when` пишется вызов метода с аргументами (к ним мы вернемся далее), чье поведение мы хотим менять. Он возвращает объект
  типа `MockerAction`, который имеет методы для объявления поведения:
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/IMockRT.java#L3-L9
* В зависимости от того, был вызван статический или динамический метод, будет меняться реализация этого интерфейса. 
  * Если это был динамический метод, то в ядре прокси объекта в мапе будет создана запись 
  <br> **((Метод, Параметры) -> (Возвращаемое значение, Тип действия))**
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockRT.java#L17-L20
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L146-L150
  * Если это был статический метод, то изменения будут адресоваться синглтону ядра статик методов. 
  Запись соответственно будет вида <br> **((Класс, Метод, Параметры) -> (Возвращаемое значение, Тип действия))**
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockRTS.java#L15-L18
  https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockStaticCore.java#L127-L130
* `MockerAction.then*(...)` - разберем вариации поведения 
  * `.thenNull()` - функция будет возвращать `null`. Изначальное поведение всех методов.
  * `.thenReturn(R value)` - функция будет возвращать `value`. 
  * `.thenThrow(Throwable exception)` - функция будет выкидывать `exception`.
  * `.thenImplemented()` - функция будет работать с поведением изначального класса. У статика это возможно, 
  поскольку коллбэк не заменяет изначальную функцию, а встаёт перед ней

https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockStaticCore.java#L71-L86
https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L130-L142

### Аргументы

В аргументы функции можно передать 2 вида параметров:
* Готовый собранный объект: `...when(someClass.someMethod(123))...`
* Или же закинуть туда `Mocker.any()`

`Mocker.any(Class someClass)` - позволяет указать, что в этом параметре может быть любое значение типа `someClass` 
для отработки дальнейшего поведения. <br>
На словах реализация следующая: 
* `.any()` порождает объект выбранного типа по пустому конструктору. Для этого передаваемые классы должны быть либо врапперами, либо должны иметь пустой конструктор.
https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/Mocker.java#L118-L164
* Этот объект записывается в общий пулл объектов, которые были порождены для данного метода.
* При создания записи о действии в ядре, так же создаётся "маска", по которой в дальнейшем будут проверяться вызовы. (На этом этапе опустошается пул генерированных объектов)
https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L22-L25
https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/main/java/com/mocker/core/MockCoreInstance.java#L170-L187
* На основе маски генерируются ключи для получения записей из мапы ядра.
* Приоритетность масок идет по возрастанию количества использованных any() при вызове. (То есть сначала проверка полного совпадения N аргументов, потом N-1 и так до 0) 
* На каждый "слой" может существовать только одно поведение. и пример ниже объясняет почему.
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

## Демонстрационный код

https://github.com/anast3t/SimpleMockFramework/blob/346eab067e8aed967effe7785682b0fbf246803b/src/test/java/MockTest.java#L13

## Распределение обязанностей

Делали коллективно за одним столом.