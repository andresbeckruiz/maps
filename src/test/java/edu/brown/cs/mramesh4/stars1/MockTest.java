package edu.brown.cs.mramesh4.stars;
//import edu.brown.cs.mramesh4.stars.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.After;
import java.util.List;
import java.util.ArrayList;
import edu.brown.cs.mramesh4.MockPerson.*;



public class MockTest{
  private MockPerson mp = new MockPerson("Test", "Test","java@gmail.com", "01/02/2001",  "Male", "3765 Street", "32.17.18.2953");
  //test if the mockperson is empty
  @Test
  public void testValidEmpty() {
    assertFalse(mp.isValidGender(""));
    assertFalse(mp.isValidDate(""));
    assertFalse(mp.isValidName("", ""));
    assertFalse(mp.isValidName("Mit", ""));
    assertFalse(mp.isValidName("", "Mit"));
    assertFalse(mp.isValidIp(""));
    assertFalse(mp.isValidEmail(""));
    assertFalse(mp.isValidEmail(" "));

    assertFalse(mp.isValidGender(null));
    assertFalse(mp.isValidDate(null));
    assertFalse(mp.isValidName(null, "Mit"));
    assertFalse(mp.isValidName("Mit", null));
    assertFalse(mp.isValidName(null, null));
    assertFalse(mp.isValidIp(null));
    assertFalse(mp.isValidEmail(null));

  }
  //test to assure invalid date is thrown
  @Test
  public void testInvalidDate() {
    assertFalse(mp.isValidDate("01.02.03"));
    assertFalse(mp.isValidDate("02/05/0000001"));
    assertFalse(mp.isValidDate("May 25th 2020"));
    assertFalse(mp.isValidDate("02/sauce/orange"));
    assertFalse(mp.isValidDate("0345/203/43604"));
  }

  //test for exception with bad args
  @Test(expected = IllegalArgumentException.class)
  public void ExceptionThrown(){
    MockPerson mp2 = new MockPerson("", "", "", "", "", "", "");
  }
  //test for exception with bad args
  @Test(expected = IllegalArgumentException.class)
  public void ExceptionThrownWithBadDate(){
    MockPerson mp3 = new MockPerson("Test", "Test","java@gmail.com", "01022001",  "Male", "3765 Street", "32.17.18.2953");
  }

 // @Test
  public void testToString(){
    String res = "Name: " + "Test" + " " + "Test"
      + ","  + "Address:" + " " +  "3765 Street" + ","
      + "Gender:" + " " +  "Male" + ","
      + "E-mail:" + " " +  "java@gmail.com" + ","
      + "Date:" + " " +  "01/02/2001" + ","
      + "Ip-Address:" + " " +  "32.17.18.2953";
    assertEquals(mp.toString(), res);
  }


}