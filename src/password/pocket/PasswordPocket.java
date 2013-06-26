package password.pocket;

import serialization.FileManipulator;
import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import javax.swing.JOptionPane;

/**
 * A PasswordPocket contains a HashMap of things and the passwords that unlock
 * them.
 *
 * @author Jacob
 */
public class PasswordPocket implements Serializable {

    public static final String PROGRAM_NAME = "Pineapple Password Pocket";
    public static final String PASSWORD_FILE_NAME = "passwords.data";
    public static final String PATH = "";
    //Aa ArrayList that stores passwords and the things that they unlock
    private ArrayList<Entry> passwords;
    //Cryptography
    private Cipher pbeCipher;
    private SecretKey pbeKey;
    private PBEParameterSpec pbeParamSpec;
    private PBEKeySpec pbeKeySpec;

    /**
     * Creates a new empty PasswordPocket, and generate a key from the user's
     * plaintext password.
     */
    public PasswordPocket(String masterPassword) {
        //Don't allow empty passwords
        if (masterPassword.equals("")) {
            System.exit(1);
        }
        //Initalize cryptography variables
        pbeParamSpec = new PBEParameterSpec("seven777".getBytes(), 77); // salt
        pbeKeySpec = new PBEKeySpec(masterPassword.toCharArray()); // plaintext password
        try {
            //Generate Key
            SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
            pbeKey = keyFac.generateSecret(pbeKeySpec);
            //Create a cipher to unlock the encrypted passwords, then initialize it
            pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeySpecException ex) {
            Logger.getLogger(PasswordPocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        //Create an empty HashMap in case an existing file can't be loaded
        passwords = new ArrayList<>();
    }

    /**
     * @return the HashMap that holds password list
     */
    public ArrayList<Entry> getPasswords() {
        return passwords;
    }

    /**
     * Seals the passwords in an encrypted SealedObject before saving them to a
     * file.
     */
    public void savePasswords() {
        try {
            // Initialize PBE Cipher with key and parameters
            pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);
            SealedObject sealedObject = new SealedObject(passwords, pbeCipher);
            FileManipulator.writeObject(sealedObject, PATH, PASSWORD_FILE_NAME);
        } catch (IOException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(PasswordPocket.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Attempts to decrypt and load passwords if there's an existing password
     * file.
     *
     * return true if the password file was found
     */
    public boolean loadPasswords() {
        //Check to see if a SealedObject has already been saved in the working directory
        if (FileManipulator.fileExists(PATH, PASSWORD_FILE_NAME)) {
            try {
                //Deserialize the SealedObject
                SealedObject so = (SealedObject) FileManipulator.readObject(PATH, PASSWORD_FILE_NAME);
                //Retrieve the password data from the SealedObject
                pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
                passwords = (ArrayList<Entry>) so.getObject(pbeCipher);
                return true;
            } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | InvalidKeyException | InvalidAlgorithmParameterException ex) {
                Logger.getLogger(PasswordPocket.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                JOptionPane.showMessageDialog(null, "Invalid master password.", "Invalid password.", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        return false;
    }

    /**
     * Returns a HashMap<String,String> of password/lock combos where the search
     * term was part of the lock name.
     *
     * @param searchTerm a part of the lock being searched for
     * @return a HashMap<String,String> of results
     */
    public ArrayList<Entry> searchPasswords(String searchTerm) {
        //Make the search term lower case for easier searching
        searchTerm = searchTerm.toLowerCase();
        //Create a HashMap to hold search results
        ArrayList<Entry> results = new ArrayList<>();
        //Search through the list, looking for locks that contain the search term
        for (Entry nextLock : passwords) {
            String location = nextLock.getLocation();
            //Check to see if the search term is included in the list
            if (location.toLowerCase().contains(searchTerm)) {
                //Add the password/source to the result list
                results.add(nextLock);
            }
        }
        return results;
    }

    /**
     * @return true if the password file exists
     */
    static public boolean passwordFileExists() {
        return FileManipulator.fileExists(PATH, PASSWORD_FILE_NAME);
    }

    /**
     * Adds the passed information as a new Entry in the PasswordPocket.
     *
     * @param location
     * @param login
     * @param password
     */
    public void addEntry(String location, String login, String password) {
        passwords.add(new Entry(location, login, password));
    }

    /**
     * Adds the passed entry to the password list.
     *
     * @param entry
     */
    public void addEntry(Entry entry) {
        passwords.add(entry);
    }

    /**
     * Removes the passed entry if it is in the password list.
     *
     * @param entry
     */
    public void removeEntry(Entry entry) {
        passwords.remove(entry);
    }

    /**
     * Returns true if the PasswordPocket contains the passed login and
     * location.
     *
     * @param login
     * @param location
     * @return true if the login and location are already present
     */
    public boolean contains(String login, String location) {
        for (Entry e : passwords) {
            if (e.getLogin().equalsIgnoreCase(login) && e.getLocation().equalsIgnoreCase(location)) {
                return true;
            }
        }
        return false;
    }
}
