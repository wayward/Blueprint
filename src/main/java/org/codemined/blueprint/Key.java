package org.codemined.blueprint;

import java.lang.annotation.*;

/**
 * Sets the name of the key to associate with the annotated method.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Key {

  public String value();

}
