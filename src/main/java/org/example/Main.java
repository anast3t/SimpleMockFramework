package org.example;

import com.mocker.core.MockCoreInstance;

public class Main {
    public static void main(String[] args) throws Throwable {
/*        Object[] arr1 = new Object[]{1, 2, 3};
        Object[] arr2 = new Object[]{1, 2, 3};

        ArrayList<Object> arrayList1 = new ArrayList<>(Arrays.asList(arr1));
        ArrayList<Object> arrayList2 = new ArrayList<>(Arrays.asList(arr2));
        ArrayList<Object> arrayList3 = new ArrayList<>(Arrays.asList(arr1));
        ArrayList<Object> arrayList4 = new ArrayList<>(Arrays.asList(arr2));

        System.out.println( Objects.hash(arr1));
        System.out.println( Objects.hash(arr2));
        System.out.println(Objects.hash(arrayList1, arrayList2));
        System.out.println(Objects.hash(arrayList3, arrayList4));

        Method method = SomeClass.class.getMethod("testPrint");

        Pair<Method, ArrayList<Object>> pair1 = new Pair<>(method, arrayList3);
        Pair<Method, ArrayList<Object>> pair2 = new Pair<>(method, arrayList2);
        System.out.println(pair1.hashCode());
        System.out.println(pair2.hashCode());

        ArrayList<Object> arrayListI = new ArrayList<>(List.of(new ArrayList<Object>(List.of(1, 2, 3)), 1, 2, 3));
        ArrayList<Object> arrayListI2 = new ArrayList<>(List.of(new ArrayList<Object>(List.of(1, 2, 3)), 1, 2, 3));
        System.out.println(arrayListI.hashCode());
        System.out.println(arrayListI2.hashCode());

        ArrayList<Object> objectArrayList1 = new ArrayList<>(Arrays.asList(new Object[]{new Object[]{arr1, arr2}, new Object[]{arr1, arr2}}));
        ArrayList<Object> objectArrayList2 = new ArrayList<>(Arrays.asList(new Object[]{new Object[]{arr1, arr2}, new Object[]{arr1, arr2}}));
        System.out.println(objectArrayList1.hashCode());
        System.out.println(objectArrayList2.hashCode());*/




        SomeClass someClass = new SomeClass();

        MockCoreInstance<SomeClass> core = new MockCoreInstance<>(SomeClass.class, someClass);

        someClass = core.getMock();

        core.when(someClass.integerReturnMethod(1)).thenReturn(123);
        System.out.println(someClass.integerReturnMethod (1));

        core.when(someClass.integerReturnMethod (1)).thenNull();
        System.out.println(someClass.integerReturnMethod (1));

        core.when(someClass.integerReturnMethod (1)).thenImplemented();
        System.out.println(someClass.integerReturnMethod (1));

//        core.when(someClass.integerReturnMethod (1)).thenThrow(new Exception("suffering from success"));
//        System.out.println(someClass.integerReturnMethod (1));

//        core.when(someClass.integerReturnMethod ()).thenThrow(new Exception("123"));
//        System.out.println(someClass.integerReturnMethod ());

    }
}

// TODO: Придумать че делать с парами в статике (возможно триплет ввести, возможно отдельные классы),
//  вынести в отдельный метод equals в паре
//  внедрить ActionType
