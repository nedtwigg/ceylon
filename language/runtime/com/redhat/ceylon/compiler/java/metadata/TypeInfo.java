package com.redhat.ceylon.compiler.java.metadata;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation applied to a Java typed declaration (such as a method parameter 
 * or result) recording the Ceylon type of the declaration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD})
public @interface TypeInfo {
    
    /** 
     * String representation of the Ceylon type that the annotated element has
     */
    String value() default "";
    
    /**
     * Whether the declared method type was {@code void}
     */
    boolean declaredVoid() default false;
    
    /**
     * Whether the Ceylon type has been erased.
     */
    boolean erased() default false;
    
    /**
     * Set to true to get the same special handling as Java method return types wrt. null elements 
     */
    boolean uncheckedNull() default false;
    
    /**
     * Whether the declared type is untrusted.
     */
    boolean untrusted() default false;
}
