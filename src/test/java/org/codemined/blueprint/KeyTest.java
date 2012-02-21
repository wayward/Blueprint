package org.codemined.blueprint;

import org.testng.annotations.Test;

/**
 * Tests for the Key annotation.
 *
 * @author Zoran Rilak
 * @version 0.1
 * @since 0.1
 */
@Test
public class KeyTest {

  private interface Iface {
    @Key("key") void aMethod();
  }

  //TODO mock out a Source and test @Key

}
