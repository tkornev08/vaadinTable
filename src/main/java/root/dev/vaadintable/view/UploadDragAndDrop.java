package root.dev.vaadintable.view;


import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import root.dev.vaadintable.services.UploadExamplesI18N;

public class UploadDragAndDrop extends Div {

    public UploadDragAndDrop() {
        MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAutoUpload(false);

        Button uploadAllButton = new Button("Upload All Files");
        uploadAllButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        uploadAllButton.addClickListener(event -> {
            // No explicit Flow API for this at the moment
            upload.getElement().callJsFunction("uploadFiles");
        });

        UploadExamplesI18N i18n = new UploadExamplesI18N();
        i18n.getAddFiles().setMany("Select Files...");
        upload.setI18n(i18n);


        add(upload, uploadAllButton);
    }

}
