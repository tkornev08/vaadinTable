package root.dev.vaadintable.services;

import com.vaadin.flow.component.upload.UploadI18N;

import java.util.Arrays;

public class UploadExamplesI18N extends UploadI18N {
    public UploadExamplesI18N() {
        setDropFiles(new DropFiles().setOne("Drop file here")
                .setMany("Drop files here"));
        setAddFiles(new AddFiles().setOne("Upload File...")
                .setMany("Upload Files..."));
        setError(new Error().setTooManyFiles("Too Many Files.")
                .setFileIsTooBig("File is Too Big.")
                .setIncorrectFileType("Incorrect File Type."));
        setUploading(new Uploading()
                .setStatus(new Uploading.Status().setConnecting("Connecting...")
                        .setStalled("Stalled")
                        .setProcessing("Processing File...").setHeld("Queued"))
                .setRemainingTime(new Uploading.RemainingTime()
                        .setPrefix("remaining time: ")
                        .setUnknown("unknown remaining time"))
                .setError(new Uploading.Error()
                        .setServerUnavailable(
                                "Upload failed, please try again later")
                        .setUnexpectedServerError(
                                "Upload failed due to server error")
                        .setForbidden("Upload forbidden")));
        setUnits(new Units().setSize(Arrays.asList("B", "kB", "MB", "GB", "TB",
                "PB", "EB", "ZB", "YB")));
    }
}
