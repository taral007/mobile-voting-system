/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fel.mvod.gui.settings;

import cz.cvut.fel.mvod.global.GlobalSettingsAndNotifier;
import cz.cvut.fel.mvod.gui.settings.panels.PrologueSettingsPanel;
import cz.cvut.fel.mvod.prologueServer.PrologueServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import cz.cvut.fel.mvod.net.Server;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Murko
 */
public class CertManager {

    public final static int PROLOGUE = 0;
    public final static int VOTING = 1;

    private CertManager() {
    }

    public static void changeCert(int system) throws FileNotFoundException {
        boolean loadOK = false;
        String passphrase = "11111";
        int tries = 3;
        JFileChooser fc = null;
        int returnVal = 0;
        File file;
        while (!loadOK) {
            if (tries == 3) {
                fc = new JFileChooser();
                returnVal = fc.showOpenDialog(null);
                file = null;
                tries = 0;
            }
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {

                    file = fc.getSelectedFile();
                    KeyStore ks = KeyStore.getInstance("PKCS12");
                    ks.load(new FileInputStream(file.getAbsolutePath()), passphrase.toCharArray());
                    switch (system) {
                        case PROLOGUE: {
                            GlobalSettingsAndNotifier.singleton.modifySettings("Prologue_certpath", file.getAbsolutePath(), true);
                            PrologueServer.setCertPass(passphrase);
                            break;
                        }
                        case VOTING: {
                            GlobalSettingsAndNotifier.singleton.modifySettings("Voting_certpath", file.getAbsolutePath(), true);
                            Server.setCertPass(passphrase);
                            break;

                        }
                    }
                    loadOK = true;
                } catch (KeyStoreException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    passphrase = JOptionPane.showInputDialog(null, GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallLabel"), GlobalSettingsAndNotifier.singleton.messages.getString("certPassChallTitle"), JOptionPane.WARNING_MESSAGE);
                    tries++;
                } catch (NoSuchAlgorithmException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                } catch (CertificateException ex) {
                    Logger.getLogger(PrologueSettingsPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (returnVal == JFileChooser.CANCEL_OPTION) {
                break;
            }
        }

    }
}