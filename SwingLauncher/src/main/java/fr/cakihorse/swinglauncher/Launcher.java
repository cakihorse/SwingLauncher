package fr.cakihorse.swinglauncher;

import fr.cakihorse.swinglauncher.utils.Random;
import fr.flowarg.flowlogger.ILogger;
import fr.flowarg.flowlogger.Logger;
import fr.flowarg.flowupdater.FlowUpdater;

import fr.flowarg.flowupdater.versions.VanillaVersion;

import fr.litarvan.openauth.microsoft.MicrosoftAuthResult;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticationException;
import fr.litarvan.openauth.microsoft.MicrosoftAuthenticator;

import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.*;
import fr.theshark34.openlauncherlib.util.CrashReporter;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;


import static fr.cakihorse.swinglauncher.Main.getSaver;


public class Launcher extends Component {
    private static GameInfos gameInfos = new GameInfos("launcherswing", new GameVersion("1.8.8", GameType.V1_8_HIGHER), new GameTweak[]{});
    private static Path path = gameInfos.getGameDir();
    public static File crashFile = new File(String.valueOf(path), "crashes");
    private static CrashReporter cReporter = new CrashReporter(String.valueOf(crashFile), path);
    public static AuthInfos authInfos;





    public static void authMs() throws MicrosoftAuthenticationException {
        MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator();
        final String refresh_token = getSaver().get("refresh_token");
        MicrosoftAuthResult result = null;
        if (refresh_token != null && !refresh_token.isEmpty()) {
            result = microsoftAuthenticator.loginWithRefreshToken(refresh_token);
            authInfos = new AuthInfos(result.getProfile().getName(), result.getAccessToken(), result.getProfile().getId());
            System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
        } else {
            result = microsoftAuthenticator.loginWithWebview();
            getSaver().set("refresh_token", result.getRefreshToken());
            getSaver().set("accessToken", result.getAccessToken());
            getSaver().set("clientToken", result.getClientId());

            getSaver().save();
            authInfos = new AuthInfos(result.getProfile().getName(), result.getAccessToken(), result.getProfile().getId());
            System.out.printf("Logged in with '%s'%n", result.getProfile().getName());
        }
    }


    public static void update() throws Exception {

        final VanillaVersion vanillaVersion = new VanillaVersion.VanillaVersionBuilder()
                .withName("1.8.8")
                /*with mcp :
                .withMCP(new MCP(YourdownloadUrl, Cleintsha1, ClientSize))
                you can do a Random.generateRandomString for the sha1 but the client will be downloaded each restart.
                */
                .build();
        final ILogger logger = new Logger("[LAUNCHER]", null);
        //for more information about the update, join this discord : https://discord.gg/CS5NxapkDU
        final FlowUpdater updater = new FlowUpdater.FlowUpdaterBuilder()
                .withVanillaVersion(vanillaVersion)
                .withLogger(logger)
                .build();
        updater.update(path);
    }

    public static void launch() throws Exception {
        ExternalLaunchProfile profile = MinecraftLauncher.createExternalProfile(gameInfos, GameFolder.FLOW_UPDATER, authInfos);
        //add ram from saver to args
        profile.getVmArgs().addAll(Arrays.asList(new String[]{"-Xms1G", "-Xmx" + getSaver().get("ram") + "G"}));
        ExternalLauncher launcher = new ExternalLauncher(profile);

        //launch Minecraft
        launcher.launch();
    }


    public static void authCrack() {
        /*
         * WARNING: If you want the users to use their own username (entered in a text field, for example),
         * you will need to handle it yourself. In this context, we generate a new username every time
         * the app is restarted.
         */
        authInfos = new AuthInfos(Random.generateRandomString(10), Random.generateRandomAccesToken(10),Random.generateRandomUUID());
    }

    public static Path getPath() {
        return path;
    }

}
