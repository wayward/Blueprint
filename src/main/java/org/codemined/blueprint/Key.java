package org.codemined.blueprint;

import java.lang.annotation.*;

/**
 * Overrides the name of the sub-key onto which the method will be mapped.
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
