/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

/**
 * Tests the contents of this class.
 *
 * @author johnmichaelreed2
 */
class PackagePrivateUnitTest {
    public static void main(String[] args) {
        Print.myDefaultPrintStreamSetter(Print.DefaultPrintStream.ONLY_STANDARD_ERROR);
        Print.exception(new Exception("I am a new exception"), "New exception detected");
        Print.exception(new Exception("I am a new exception2"));
        Print.bad("This is bad.");
        Print.slightlyBad("This is slightly bad.");
        Print.good("This is good.");
        Print.slightlyGood("This is slightly good.");
        Print.veryGood("This is very good.");
        Tester.check(false);
    }
}
