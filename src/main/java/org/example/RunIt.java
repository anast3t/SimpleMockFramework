package org.example;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//annotating the annotation:
//where it could be applied?
@Target(ElementType.METHOD)
//and when to erase it?
@Retention(RetentionPolicy.RUNTIME)
//the annotation itself
public @interface RunIt {
}