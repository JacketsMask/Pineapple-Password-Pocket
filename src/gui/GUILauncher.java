package gui;

import password.pocket.Entry;
import password.pocket.PasswordPocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * A program that allows the user to store and encrypt password information, and
 * then quickly search for it and retrieve it later.
 *
 * @author Jacob
 */
public class GUILauncher {

    public static void createTestValues(PasswordPocket pocket) {
        pocket.addEntry("test1.com", "user", "test_password1");
        pocket.addEntry("test2.com", "user", "test_password2");
        pocket.addEntry("test3.com", "user", "test_password3");
        pocket.savePasswords();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        //Set platform appropriate GUI options
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(GUILauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        //First run time welcome message
        if (!PasswordPocket.passwordFileExists()) {
            JOptionPane.showMessageDialog(null, "Thank you for choosing "
                    + PasswordPocket.PROGRAM_NAME + " for your password storage"
                    + " needs!\nYou will need to set a master password before "
                    + "other passwords can be stored.  \nPlease keep your master "
                    + "password secure, as it is the only way to access your "
                    + "encrypted passwords.", null, JOptionPane.INFORMATION_MESSAGE);
        }
        //Verify that a legitimate password is chosen
        char[] input;
        do {
            //Create a dialog to capture receive master password
            MasterPasswordJDialog dialog = new MasterPasswordJDialog(null, true);
            dialog.setLocationByPlatform(true);
            dialog.setVisible(true);
            input = dialog.getInput();
        } while (input.equals(""));
        //Use the master password to attempt to decrypt the password file if it exists
        PasswordPocket pocket = new PasswordPocket(input);
        if (PasswordPocket.passwordFileExists()) {
            pocket.loadPasswords();
            for (Entry e : pocket.getPasswords()) {
                System.out.println(e);
            }
        } else {
            //If it's a new password pocket, generate some junk entries
            createTestValues(pocket);
        }
        //Create and set the HomeFrame to be visible
        HomeFrame homeFrame = new HomeFrame(pocket);
        homeFrame.setLocationByPlatform(true);
        homeFrame.setVisible(true);
    }
}
