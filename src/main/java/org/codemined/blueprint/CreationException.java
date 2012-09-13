package org.codemined.blueprint;

/**
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
public class CreationException extends BlueprintException {

  @Override
  public CreationException(String message) {
    super(message);
  }

  @Override
  public CreationException(String message, Throwable cause) {
    super(message, cause);
  }

}
