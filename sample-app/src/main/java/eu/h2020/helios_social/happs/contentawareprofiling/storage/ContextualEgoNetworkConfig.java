package eu.h2020.helios_social.happs.contentawareprofiling.storage;

import java.io.File;

public class ContextualEgoNetworkConfig implements InternalStorageConfig {

    private final File storageDir;

    public ContextualEgoNetworkConfig(File storageDir) {
        this.storageDir = storageDir;
    }

    @Override
    public File getStorageDir() {
        return this.storageDir;
    }

}
