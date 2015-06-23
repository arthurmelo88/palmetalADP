package org.adempierelbr.nfe.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD )
@Retention(RetentionPolicy.RUNTIME)
public @interface NFeGroup {
	boolean isMandatory() default false;
	int numOcorrencias() default 0;
	String description();
}
