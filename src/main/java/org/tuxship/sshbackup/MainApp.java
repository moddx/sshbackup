package org.tuxship.sshbackup;

import javafx.application.Application;
import javafx.stage.Stage;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.tuxship.sshbackup.ui.MainWindowProvider;

import javax.crypto.Cipher;
import javax.inject.Named;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Provider;
import java.security.Security;

/**
 * Created by Matthias Ervens on 16.01.2017.
 */
@Named
public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

    public static final String version = "0.1.1";

    private static Stage mainStage;
    private ApplicationContext ctx;

    @Override
    public void start(Stage primaryStage) throws Exception {
        mainStage = primaryStage;

        ctx = new AnnotationConfigApplicationContext(MainConfiguration.class);

        MainWindowProvider winProv = ctx.getBean(MainWindowProvider.class);
        winProv.loadToStage(mainStage);
        mainStage.setMinWidth(450d);
        mainStage.setMinHeight(320d);

//        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/logo.png")));
    }

    public static Stage getMainStage() {
        return mainStage;
    }

    public static void main(String[] args) {
        // hack for JCE Unlimited Strength
        try {
            Field field = Class.forName("javax.crypto.JceSecurity").getDeclaredField("isRestricted");

            field.setAccessible(true);

            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

            field.set(null, false);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            logger.error("Exception while enabling JCE Unlimited Strength via  Reflection.", e);
        }


//        BouncyCastleProvider bouncyCastleProvider = new BouncyCastleProvider();
//        Security.addProvider(bouncyCastleProvider);
//
//        logger.debug("\nSecurity-Provider:");
//        for(Provider prov : Security.getProviders()) {
//            logger.debug( "{}: {}", prov.getName(), prov.getInfo());
//        }
//
//        try {
//            logger.debug("\nMaxAllowedKeyLength (for '" + Cipher.getInstance("AES").getProvider() + "' using current 'JCE Policy Files'):\n"
//                    + "  DES        = " + Cipher.getMaxAllowedKeyLength("DES") + "\n"
//                    + "  Triple DES = " + Cipher.getMaxAllowedKeyLength("Triple DES") + "\n"
//                    + "  AES        = " + Cipher.getMaxAllowedKeyLength("AES") + "\n"
//                    + "  Blowfish   = " + Cipher.getMaxAllowedKeyLength("Blowfish") + "\n"
//                    + "  RSA        = " + Cipher.getMaxAllowedKeyLength("RSA") + "\n");
//        } catch (Exception e) {
//            logger.error("Exception while printing cipher/JCE debug information.", e);
//        }

        launch(args);

    }
}
