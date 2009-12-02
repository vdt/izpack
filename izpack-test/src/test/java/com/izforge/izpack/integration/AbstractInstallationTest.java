package com.izforge.izpack.integration;

import com.izforge.izpack.AssertionHelper;
import com.izforge.izpack.bootstrap.ApplicationContainer;
import com.izforge.izpack.bootstrap.IApplicationContainer;
import com.izforge.izpack.bootstrap.IPanelContainer;
import com.izforge.izpack.compiler.CompilerConfig;
import com.izforge.izpack.data.ResourceManager;
import com.izforge.izpack.installer.base.InstallerFrame;
import com.izforge.izpack.installer.base.LanguageDialog;
import org.apache.commons.io.FileUtils;
import org.fest.swing.fixture.DialogFixture;
import org.fest.swing.fixture.FrameFixture;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.MethodRule;
import org.junit.rules.Timeout;

import java.io.File;

/**
 * Abstract test for integration test
 */
public class AbstractInstallationTest {
    private File currentDir = new File(getClass().getClassLoader().getResource(".").getFile());

    protected static final String APPNAME = "Test Installation";

    @Rule
    public MethodRule globalTimeout = new Timeout(60000);
    protected IApplicationContainer applicationContainer;
    protected IPanelContainer panelContainer;

    protected FrameFixture installerFrameFixture;
    protected DialogFixture dialogFrameFixture;

    @Before
    public void initBinding() throws Throwable {
        applicationContainer = new ApplicationContainer();
        applicationContainer.initBindings();
    }

    @Before
    public void deleteLock() {
        File file = new File(System.getProperty("java.io.tmpdir"), "iz-" + LanguageSelectionTest.APPNAME + ".tmp");
        file.delete();
    }

    /**
     * Prepare fest fixture for installer frame
     *
     * @throws Exception
     */
    protected void prepareFrameFixture() throws Exception {
        InstallerFrame installerFrame = panelContainer.getComponent(InstallerFrame.class);
        installerFrame.loadPanels();
        installerFrameFixture = new FrameFixture(installerFrame);
        installerFrameFixture.show();
    }

    /**
     * Prepare fest fixture for lang selection
     */
    protected void prepareDialogFixture() {
        LanguageDialog languageDialog = panelContainer.getComponent(LanguageDialog.class);
        dialogFrameFixture = new DialogFixture(languageDialog);
        dialogFrameFixture.show();
    }

    /**
     * Compile an installer and unzip the created jar.
     *
     * @param installationFile The izpack installation file
     * @param workingDirectory
     * @throws Exception
     */
    protected void compileAndUnzip(String installationFile, File workingDirectory) throws Exception {
        File installerFile = new File(workingDirectory, installationFile);
        File out = new File(workingDirectory, "out.jar");
        compileAndUnzip(installerFile, workingDirectory, out);
    }

    protected File getWorkingDirectory(String workingDirectoryName) {
        File workingDirectory = new File(getClass().getClassLoader().getResource(workingDirectoryName).getFile());
        setResourcePath(workingDirectory);
        return workingDirectory;
    }

    /**
     * Compile an installer and unzip it.
     *
     * @param installerFile    The izpack installer file
     * @param workingDirectory The directory containing the installer file
     * @param out              The output of the compiler
     * @throws Exception
     */
    private void compileAndUnzip(File installerFile, File workingDirectory, File out) throws Exception {
        CompilerConfig c = new CompilerConfig(installerFile.getAbsolutePath(), workingDirectory.getAbsolutePath(), "default", out.getAbsolutePath());
        c.executeCompiler();
        File extractedDir = new File(workingDirectory, "temp");
        // Clean before use
        FileUtils.deleteDirectory(extractedDir);
        extractedDir.mkdirs();
        AssertionHelper.unzipJar(out, extractedDir);
    }

    /**
     * Set resource path in resource manager
     *
     * @param baseDir Base path where unzip has been operated
     */
    protected void setResourcePath(File baseDir) {
        String relativePath = baseDir.getAbsolutePath().substring(currentDir.getAbsolutePath().length());
        System.out.println(relativePath);
        applicationContainer.getComponent(ResourceManager.class).setResourceBasePath(relativePath + "/temp/resources/");
    }
}
