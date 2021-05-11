package ru.senina.itmo.lab7.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark command classes
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CommandAnnotation {
    String name();

    boolean element() default false;

    boolean id() default false;

    boolean index() default false;

    boolean description() default false;

    boolean filename() default false;

    boolean collectionKeeper() default false;

    boolean parser() default false;

    boolean isVisibleInHelp() default true;
}