package com.example.paragony;

import com.example.paragony.databinding.ActivityMainBinding;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import androidx.fragment.app.FragmentTransaction;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;

import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import com.google.mlkit.nl.entityextraction.EntityExtraction;
import com.google.mlkit.nl.entityextraction.EntityExtractor;
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions;


import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements ParagraphEditDialog.ParagraphEditDialogListener,
        WaitingRoomPushDialog.WaitingRoomPushListener {

    ActivityMainBinding binding;




    ParagonyDatabaseTools paragonyDatabaseTools;

    String CROPPED;

    @Override
    public void applyData(String newFileName) {

    }


    @Override
    public void applyData(String cropped, LinearLayout parent, int index) {


        ParagraphView paragraphView = new ParagraphView(this);


        LinearLayout.LayoutParams paragraphViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        paragraphView.setLayoutParams(paragraphViewParams);

        paragraphView.setTitleText(cropped);

        parent.addView(paragraphView, index + 1, paragraphViewParams);

        int addFreame_id = View.generateViewId();
        paragraphView.setId(addFreame_id);

        paragraphView.setOnLongClickListener(longClickListener);
        paragraphView.setOnClickListener(showEdit);

        showEdit(paragraphView);

    }

    //STATICS

    Uri imageUri;

    int INDEX;
    String DIRTCTORY = "";
    float draggedY;
    float baseDraggedY;
    float targetY;
    int[] ID = new int[1];

    
    private static final String TARGET_FOLDER = "/paragony/";
    int GRID;

    LinearLayout text_board = null;
    RelativeLayout subBoard = null;
    ScrollView scrollBoard = null;


    public static final MediaType JSON = MediaType.get("application/json");


    ActivityResultLauncher<CropImageContractOptions> cropImage =
            registerForActivityResult(new CropImageContract(),
                    result -> {


                        if (result.isSuccessful()) {

                            Bitmap cropped = BitmapFactory.decodeFile(result.getUriFilePath(getApplicationContext(), true));

                            //   saveCroppedImage(cropped);

                            getTextFromImage(cropped);

                        }
                    });


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.option_menu, menu);

        return true;
    }

    @SuppressLint("Recycle")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//MENU

        if (item.getItemId() == R.id.ocr_settings) {

            Intent intent = new Intent(this, ManagerOCR.class);
            startActivity(intent);

        }
        if (item.getItemId() == R.id.db_settings) {

            Intent intent = new Intent(this, DatabaseManager.class);
            startActivity(intent);


        }
        if (item.getItemId() == R.id.tax_settings) {
            Intent intent = new Intent(this, TaxManager.class);
            startActivity(intent);

        }

        if (item.getItemId() == R.id.pop) {

            Intent intent = new Intent(this, JsonList.class);

            Bundle out = new Bundle();

            saveBoard(out, false);


            intent.putExtras(out);

            startActivity(intent);

            out.clear();

        }


        if (item.getItemId() == R.id.push) {

            String fileName = DIRTCTORY == "" ? null : DIRTCTORY;

            WaitingRoomPushDialog waitingRoomPushDialog = new WaitingRoomPushDialog(this,text_board,fileName);

            waitingRoomPushDialog.show(getSupportFragmentManager(), "waitingRoomPushDialog");

       //     new SaveDialogWindow(MainActivity.this, text_board, fileName);

        }

        if (item.getItemId() == R.id.add_to_db) {

            List<List<Paragraph_schema>> textBoard = getBoardSchema(text_board);

            DataBaseModel dataBaseModel = new DataBaseModel(this, textBoard);

            ReceiptSchema recipt = dataBaseModel.receipt;

         //   ReceiptCheckDialog receiptCheckDialog = new ReceiptCheckDialog(recipt);

            ReceiptCheckDialog  receiptCheckDialog = ReceiptCheckDialog.newInstance(recipt);

            receiptCheckDialog.show(getSupportFragmentManager(), "ReceiptCheckDialog");


        }


        if (item.getItemId() == R.id.image_cropper) {

            if (isPermitted()) {

                showCanhubDialog();

            } else {


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    requestAndroid11StoragePermission();

                } else {

                    requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    requestPermission.launch(Manifest.permission.CAMERA);

                }
            }
        }


        return super.onOptionsItemSelected(item);
    }

    private void saveBoard(Bundle out, Boolean local) {

        if (local) {

            StoreOcr storeOcr = new StoreOcr(this);
            storeOcr.readLines(text_board);
            Paragraph_schema[][] board = storeOcr.lines;

            int boardLength = board.length;

            out.putInt("boardLength", boardLength);

            for (int i = 0; i < boardLength; i++) {

                out.putParcelableArray(String.format("%d", i), board[i]);

            }

        } else {


            int longestLine = 0;

            StoreOcr storeOcr = new StoreOcr(this);

            storeOcr.readLines(text_board);

            Paragraph_schema[][] board = storeOcr.lines;


            int boardLength = board.length;


            out.putInt("boardLength", boardLength);

            for (int i = 0; i < board.length; i++) {

                Paragraph_schema[] line = board[i];

                for (int j = 0; j < line.length; j++) {

                    if (j > longestLine) longestLine = j;

                    Paragraph_schema paragraphData = line[j];

                    out.putString(String.format("l=%d e=%d i=textLine", i, j), paragraphData.textLine);
                    out.putString(String.format("l=%d e=%d i=category", i, j), paragraphData.category);
                    out.putStringArray(String.format("l=%d e=%d i=tags", i, j), paragraphData.getTagStringArray());

                }
                out.putInt("longestLine", longestLine + 1);
            }
        }
    }


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        paragonyDatabaseTools = new ParagonyDatabaseTools(this);

        GRID = paragonyDatabaseTools.getRowHeight();


        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        text_board = binding.textClipboard;
        text_board.setOnDragListener(dragListener2);


        scrollBoard = binding.scrollBase;


        subBoard = binding.mainActivity;
        subBoard.setOnDragListener(scrollListener);


        binding.buttonAdd.setOnClickListener(v -> {

            Paragraph_schema[][] empty = {{new Paragraph_schema("")}};
            createFrames(empty, true);

        });


        //SAVEDINSTANCESTATE


        if (savedInstanceState != null) {


            int boardLength = savedInstanceState.getInt("boardLength");

            System.out.println(String.format("BUNDLE_LEN: %d", boardLength));


            Paragraph_schema[][] paragraphs = new Paragraph_schema[boardLength][];

            for (int i = 0; i < boardLength; i++) {

                paragraphs[i] = (Paragraph_schema[]) savedInstanceState.getParcelableArray(String.format("%d", i));

            }

            createFrames(paragraphs, false);


            savedInstanceState.clear();


        } else {


        }

        Intent intent = getIntent();

        String directory = intent.getStringExtra("directory");
        if (directory != null) {
            DIRTCTORY = directory.replace("NEW", "");
        }
        Boolean fileOpen = intent.getBooleanExtra("bool", false);
        String grid = intent.getStringExtra("GRID");
        String newFileName = intent.getStringExtra("newFileName");


        if (newFileName != null) {

            System.out.println(String.format("FILENAME: %s", newFileName));

        }

        //INTENT


        if (fileOpen != null && fileOpen) {


            StoreOcr storeOcr = new StoreOcr(this);

            if (directory.contains("NEW")) {

                System.out.println("NEW");


                storeOcr.setFilename(DIRTCTORY);

                Paragraph_schema[][] paragraphs;

                paragraphs = storeOcr.readFromJson(DIRTCTORY);

                System.out.println(String.format("ParagraphsLength: %d", paragraphs.length));

                createFrames(paragraphs, false);


                intent.removeExtra("bool");

            } else {


                storeOcr.readLines(text_board);

                Paragraph_schema[][] newBoardPart = storeOcr.readFromJson(DIRTCTORY);
                int newLen = newBoardPart.length;

                Intent extras = getIntent();


                List<Paragraph_schema[]> lines = new ArrayList<>();


                restoreBoard(extras, lines);


                for (Paragraph_schema[] line : newBoardPart) {
                    lines.add(line);
                }

                Paragraph_schema[][] mergedLines = new Paragraph_schema[lines.size()][];

                for (int i = 0; i < lines.size(); i++) {
                    mergedLines[i] = lines.get(i);
                }

                createFrames(mergedLines, false);


                intent.removeExtra("bool");
            }
        }


        if (CROPPED != null) {

            System.out.println(String.format("MainCropped: %s", CROPPED));

        }


    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(resultCode == Activity.RESULT_FIRST_USER){
//
//            System.out.println(String.format("RESULT: %s",data.getStringExtra("newFileName")));
//
//        }
//    }

    private void restoreBoard(Intent extras, List<Paragraph_schema[]> lines) {


        int boardLength = extras.getIntExtra("boardLength", 0);
        int longestLine = extras.getIntExtra("longestLine", 1);


        for (int i = 0; i < boardLength; i++) {

            Paragraph_schema[] line = new Paragraph_schema[longestLine];

            for (int j = 0; j < longestLine; j++) {

                line[j] = new Paragraph_schema(

                        extras.getStringExtra(String.format("l=%d e=%d i=textLine", i, j)),
                        extras.getStringExtra(String.format("l=%d e=%d i=category", i, j)),
                        extras.getStringArrayExtra(String.format("l=%d e=%d i=tags", i, j))
                );
            }

            List<Paragraph_schema> lineBase = new ArrayList<>();

            for (Paragraph_schema schema : line) {

                if (schema.textLine != null) {

                    lineBase.add(schema);

                }
            }

            line = new Paragraph_schema[lineBase.size()];
            for (int k = 0; k < lineBase.size(); k++) {
                line[k] = lineBase.get(k);
            }

            if (line.length != 0) {
                lines.add(line);
            }
        }
    }


    private void createFrames(Paragraph_schema[][] paragraphs, Boolean openEdit) {

        for (int i = 0; i < paragraphs.length; i++) {

            View textFrame = getLayoutInflater().inflate(R.layout.paragraph_frame_layout, null, false);
            int frame_Id = View.generateViewId();
            textFrame.setId(frame_Id);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(5, 5, 5, 5);

            textFrame.setElevation(4);


            textFrame.setOnDragListener(dragListener);
            binding.textClipboard.addView(textFrame, layoutParams);

            for (int j = 0; j < paragraphs[i].length; j++) {

                ParagraphView paragraphView;

                paragraphView = new ParagraphView(this);

                LinearLayout.LayoutParams paragraphViewParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                paragraphView.setLayoutParams(paragraphViewParams);

                LinearLayout frame = findViewById(frame_Id);


                Paragraph_schema paragraph_schema = paragraphs[i][j];

                paragraphView.putData(
                        paragraph_schema.textLine,
                        paragraph_schema.getPoints(),
                        paragraph_schema.category,
                        paragraph_schema.getTagList()
                );

                frame.addView(paragraphView, paragraphViewParams);

                int addFreame_id = View.generateViewId();
                paragraphView.setId(addFreame_id);


                paragraphView.setOnLongClickListener(longClickListener);
                paragraphView.setOnClickListener(showEdit);

                if (openEdit) {
                    showEdit(paragraphView);
                }
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle out) {

        out.clear();


        saveBoard(out, true);

        super.onSaveInstanceState(out);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {


        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onRestart() {
        super.onRestart();

    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {

        DialogFragment fragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("paragraphEditDialog");

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (fragment != null) {

            if (fragment.isAdded()) {
                Dialog dialog = fragment.getDialog();
                fragment.dismiss();
            }
        }

        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    List<List<Paragraph_schema>> getBoardSchema(LinearLayout text_board) {

        List<List<Paragraph_schema>> lines = new ArrayList<>();

        List<Paragraph_schema> paragraphs;


        for (int i = 0; i < text_board.getChildCount(); i++) {

            paragraphs = new ArrayList<>();

            LinearLayout frame = (LinearLayout) text_board.getChildAt(i);

            for (int j = 0; j < frame.getChildCount(); j++) {

                ParagraphView paragraph = (ParagraphView) frame.getChildAt(j);

                Paragraph_schema schema = new Paragraph_schema(paragraph);

                paragraphs.add(schema);
            }

            lines.add(paragraphs);
        }

        return lines;
    }

    //CROPPER MENU

    public void showCanhubDialog() {

        CropImageOptions cropImageOptions = new CropImageOptions();

        launchImageCropper(imageUri, cropImageOptions);


    }


    private void launchImageCropper(Uri uri, CropImageOptions cropImageOptions) {


        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(uri, cropImageOptions);
        cropImage.launch(cropImageContractOptions);
    }


    //OCR
    private void getTextFromImage(Bitmap bitmap) {

        EntityExtractor entityExtractor =
                EntityExtraction.getClient(
                        new EntityExtractorOptions.Builder(EntityExtractorOptions.POLISH)
                                .build());


        




        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        InputImage image = InputImage.fromBitmap(bitmap, 0);


        Task<Text> task = recognizer.process(image);

        List<Paragraph> frases = new ArrayList<>();

        binding.textClipboard.removeAllViews();

        binding.textClipboard.setOnDragListener(dragListener2);

        task.addOnSuccessListener(MainActivity.this, text -> {

            for (Text.TextBlock block : text.getTextBlocks()) {

                for (Text.Line line : block.getLines()) {

                    Paragraph paragraph = new Paragraph();
                    paragraph.putData(line.getText(), line.getCornerPoints());
                    frases.add(paragraph);
                  //  System.out.println(line.getCornerPoints());

                }
            }

            entityExtractor
                    .downloadModelIfNeeded()
                    .addOnSuccessListener(
                            aVoid -> {
                                // Model downloading succeeded, you can call the extraction API here.





                            })
                    .addOnFailureListener(
                            exception -> {
                                // Model downloading failed.
                            });








            int index = 0;
            for (Paragraph line : frases) {

                int[] baseLine = new int[1];

                int frame_Id = createFrame();

                ParagraphView paragraphView = new ParagraphView(this);
                ParagraphView paragraphView2 = new ParagraphView(this);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);

                paragraphView.setLayoutParams(params);
                paragraphView2.setLayoutParams(params);


                LinearLayout frame = findViewById(frame_Id);


                int linesCount = frases.size();

                baseLine[0] = line.getCornerPoints()[2].y;

                for (int i = index + 1; i < linesCount; i++) {

                    int[] bottom = new int[1];
                    bottom[0] = frases.get(i).getCornerPoints()[2].y;

                    if (Math.abs(bottom[0] - baseLine[0]) < GRID) {


                        if (Filter.tableAlreadyHasFrase(frame, frases.get(i).getText())) {

                            continue;

                        } else {

                            if (Filter.fraseHasSibling(frases, frases.indexOf(line), GRID)) {

                                try {


                                    int paragraph_id = View.generateViewId();
                                    paragraphView2.setId(paragraph_id);
                                    paragraphView2.putData(frases.get(i).getText() + " ", frases.get(i).getCornerPoints());
                                    frame.addView(paragraphView2);
                                    paragraphView2.setOnLongClickListener(longClickListener);
                                    paragraphView2.setOnClickListener(showEdit);

                                } catch (Exception e) {

                                    e.fillInStackTrace();
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }


                if (!Filter.tableAlreadyHasFrase(frame, line.getText())) {
                    if (Filter.fraseHasSibling(frases, frases.indexOf(line), GRID) && Filter.isFirstSibling(frases, frases.indexOf(line), GRID)) {

                        try {
                            int paragraph_id = View.generateViewId();
                            paragraphView.setId(paragraph_id);
                            paragraphView.putData(line.getText() + " ", line.getCornerPoints());
                            frame.addView(paragraphView, 0);
                            paragraphView.setOnLongClickListener(longClickListener);
                            paragraphView.setOnClickListener(showEdit);
                        } catch (Exception e) {

                            e.fillInStackTrace();
                            e.printStackTrace();
                        }
                    }
                }


                for (int c = 0; c < text_board.getChildCount(); c++) {

                    LinearLayout child = (LinearLayout) text_board.getChildAt(c);

                    if (child.getChildCount() < 1) {

                        text_board.removeView(child);

                    }
                }

                index++;
            }


//            int children = binding.textClipboard.getChildCount();
//
//
//            for (int i = 0; i < children; i++) {
//
//                View frame = binding.textClipboard.getChildAt(i);
//                View et = ((LinearLayout) frame).getChildAt(0);
//
//                points.add(((ParagraphView) et).getCornerPoints());
//                paragraphs.add(((ParagraphView) et).getText().toString());
//
//            }


            //


            //   String dataArraySet = createPayloadArray(paragraphs, points);


            //   binding.buttonCapture.setText(R.string.retake);
            //  binding.buttonSend.setVisibility(View.VISIBLE);


//            binding.buttonSend.setOnClickListener(v -> {
//
//
//                System.out.print("jsonDataToSend: ");
//                //   System.out.println(dataArraySet);
//
//                //    post(SERVER_URL_PUBLIC + "save", dataArraySet);
//
//
//            });


            recognizer.close();
        });
    }


    int createFrame() {

        View textFrame = getLayoutInflater().inflate(R.layout.paragraph_frame_layout, null, false);
        int frame_Id = View.generateViewId();
        textFrame.setId(frame_Id);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);

        textFrame.setElevation(4);

        //  textFrame.setBackgroundResource(R.drawable.shaddow_example);

        textFrame.setOnDragListener(dragListener);
        binding.textClipboard.addView(textFrame, layoutParams);


        return frame_Id;

    }


    //PAYLOAD

    private String createPayloadArray(List<String> paragraphs, List<Point[]> points) {

        Gson gson = new Gson();

        int index = 0;

        ListIterator<String> paragraphsIterator = paragraphs.listIterator();
        ListIterator<Point[]> pointIterator = points.listIterator();

        Paragraph[] paragraph = new Paragraph[paragraphs.size()];

        while (paragraphsIterator.hasNext()) {

            paragraph[index] = createParagraph(paragraphsIterator.next(), points.get(index));

            index++;

        }

        String json = gson.toJson(paragraph);

        System.out.print("JSON: ");
        System.out.println(json);

        return json;
    }


    Paragraph createParagraph(String recipe, Point[] p) {

        Paragraph paragraph = new Paragraph();
        paragraph.putData(recipe, p);

        return paragraph;

    }


    //REST

    private void post(String url, String json) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(url).post(body)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(@NonNull Call call, @NonNull IOException e) {

                                                System.out.print("EXCEPTION: ");
                                                System.err.println(e);
                                                e.fillInStackTrace();
                                                e.printStackTrace();
                                            }


                                            @Override
                                            public void onResponse(@NonNull Call call, @NonNull Response response) {

                                                System.out.print("RESPONSE: ");
                                                System.out.println(response);
                                            }

                                        }

        );

    }

    //STORAGE

    private void saveCroppedImage(Bitmap bitmap) {
        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        File myDir = new File(root + TARGET_FOLDER);


        if (!myDir.exists()) {

            boolean success = myDir.mkdirs();


            if (success) {

                System.out.println("Created:");

            } else {

                System.out.println("Cannot create");
            }

        }

        String imageName = "Image_" + new Date().getTime() + ".jpg";


        File file = new File(myDir, imageName);
        if (file.exists())


            if (file.delete()) {


            }

        try {
            // Save the Bitmap to the file
            OutputStream outputStream;


            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {


                outputStream = Files.newOutputStream(file.toPath());


            } else {


                outputStream = new FileOutputStream(file);

            }


            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);


            outputStream.flush();
            outputStream.close();


            // Add the image to the MediaStore
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Trigger a media scan to update the gallery
            MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()}, null, null);


        } catch (Exception e) {

            e.printStackTrace();

        }
    }

    //COPY

    private void copyToClipBoard(String text) {

        ClipboardManager clipBoard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Data", text);
        clipBoard.setPrimaryClip(clip);
    }

    //FRAMECREATE

    private int createEmptyFrame(int index) {

        View textFrame = getLayoutInflater().inflate(R.layout.text_clipboard_layout, null, false);

        int frame_Id = View.generateViewId();
        textFrame.setId(frame_Id);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.height = 5;
        layoutParams.setMargins(5, 5, 5, 5);


        textFrame.setBackgroundResource(R.drawable.frame_drag_entered);
        textFrame.setOnDragListener(dragListener);

        try {

            text_board.addView(textFrame, index, layoutParams);      // dodaj nową ramkę na pobranym indeksie

        } catch (Exception e) {

            e.printStackTrace();

        }
        return frame_Id;

    }

    private final void addToNewEmptyFrame(int index, View dragged, int ID) {


        LinearLayout newFrame = findViewById(ID);

        LinearLayout oldFrame = (LinearLayout) dragged.getParent(); // pobierz rodzica


        oldFrame.removeView(dragged);  // usuń się z rodzica


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );


        layoutParams.setMargins(5, 5, 5, 5);

        newFrame.setLayoutParams(layoutParams);
        newFrame.setBackgroundResource(0);
        newFrame.addView(dragged);
        dragged.setVisibility(View.VISIBLE);

        newFrame.setOnLongClickListener(longClickListener);
        newFrame.setOnDragListener(dragListener);


        for (int c = 0; c < text_board.getChildCount(); c++) {

            LinearLayout child = (LinearLayout) text_board.getChildAt(c);

            if (child.getChildCount() < 1) {

                text_board.removeView(child);

            }
        }
    }

    private int getIndex(float draggedY, float targetY) {


        int childrenCount = text_board.getChildCount();
        int[] index = new int[1];

        int[] tops = new int[childrenCount];
        int[] bottoms = new int[childrenCount];


        for (int i = 0; i < childrenCount; i++) {

            tops[i] = text_board.getChildAt(i).getTop();
            bottoms[i] = text_board.getChildAt(i).getBottom();


            if (tops[i] == targetY) {

                index[0] = i;
                break;

            }
        }
        return index[0];
    }


    //LISTENERS

    View.OnLongClickListener longClickListener = v -> {

        ClipData clipData = ClipData.newPlainText("", "");

        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);

        v.startDragAndDrop(clipData, shadowBuilder, v, 0);

        return true;
    };


    View.OnDragListener dragListener = (target, dragEvent) -> {
//111
        int event = dragEvent.getAction();
        final View dragged = (View) dragEvent.getLocalState();


        if (event == DragEvent.ACTION_DRAG_STARTED) {


            LinearLayout f = (LinearLayout) dragged.getParent();

            dragged.setVisibility(View.GONE);

            if (f.getChildCount() == 0) {
                f.setVisibility(View.GONE);
            }
        }

        if (event == DragEvent.ACTION_DRAG_LOCATION) {


            INDEX = getIndex(draggedY, target.getY());

            targetY = target.getY();

        }

        if (event == DragEvent.ACTION_DRAG_ENTERED) {


            target.setBackgroundResource(R.drawable.frame_drag_entered);
        }


        if (event == DragEvent.ACTION_DRAG_EXITED) {


            target.setBackgroundResource(0);

        }

        if (event == DragEvent.ACTION_DRAG_ENDED) {


            target.setBackgroundResource(0);

        }

        if (event == DragEvent.ACTION_DROP) {

            LinearLayout previous = (LinearLayout) dragged.getParent();
            LinearLayout current = (LinearLayout) target;

            target.setBackgroundResource(0);
            previous.removeView(dragged);

            if (previous.getChildCount() == 0 && previous != target) {
                System.out.println("GONE GONE");
                text_board.removeView(previous);

            }

            current.addView(dragged);
            dragged.setVisibility(View.VISIBLE);


            for (int c = 0; c < text_board.getChildCount(); c++) {

                LinearLayout child = (LinearLayout) text_board.getChildAt(c);

                if (child.getChildCount() < 1) {

                    text_board.removeView(child);

                }
            }
        }

        return true;
    };


//333

    View.OnDragListener scrollListener = (target, dragEvent) -> {

        int event = dragEvent.getAction();
        final View dragged = (View) dragEvent.getLocalState();


        if (event == DragEvent.ACTION_DRAG_LOCATION) {

            baseDraggedY = dragEvent.getY();


            if (baseDraggedY < subBoard.getTop() + 400) {

                scrollBoard.smoothScrollBy(0, -7);
            }

            if (baseDraggedY > subBoard.getBottom() - 300) {

                scrollBoard.smoothScrollBy(0, +7);

            }
        }

        if (event == DragEvent.ACTION_DROP) {

            ID[0] = createFrame();

            addToNewEmptyFrame(text_board.getChildCount(), dragged, ID[0]);

        }

        return true;
    };


    View.OnDragListener dragListener2 = (target, dragEvent) -> {
//222

        int event = dragEvent.getAction();
        final View dragged = (View) dragEvent.getLocalState();


        int[] coords = new int[2];
        dragged.getLocationOnScreen(coords);


        if (event == DragEvent.ACTION_DRAG_LOCATION) {

            draggedY = dragEvent.getY();


        }


        if (event == DragEvent.ACTION_DRAG_ENTERED) {


            if (draggedY < targetY) {

                ID[0] = createEmptyFrame(INDEX);
            }

            if (draggedY > targetY) {

                ID[0] = createEmptyFrame(INDEX + 1);
            }
        }

        if (event == DragEvent.ACTION_DRAG_EXITED) {


            for (int c = 0; c < text_board.getChildCount(); c++) {

                LinearLayout child = (LinearLayout) text_board.getChildAt(c);

                if (child.getChildCount() < 1) {

                    child.setVisibility(View.GONE);
                }
            }
        }

        if (event == DragEvent.ACTION_DROP) {

            addToNewEmptyFrame(INDEX, dragged, ID[0]);
        }


        return true;
    };


//EDIT

    View.OnClickListener showEdit = v ->

    {


        showEdit(v);


    };


    @SuppressLint("ResourceType")
    private void showEdit(View v) {


        ParagraphEditDialog paragraphEditDialog = ParagraphEditDialog.newInstance((ParagraphView) v);

        paragraphEditDialog.show(getSupportFragmentManager(), "paragraphEditDialog");


    }


    //PERMISSIONS

    @TargetApi(Build.VERSION_CODES.R)
    private void requestAndroid11StoragePermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
        android11StoragePermission.launch(intent);
    }

    private boolean isPermitted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            return ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private final ActivityResultLauncher<String> requestPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                    isGranted -> {

                        if (isGranted) {

                            showCanhubDialog();

                        }
                    });


    ActivityResultLauncher<Intent> android11StoragePermission =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {

                        if (isPermitted()) {

                            showCanhubDialog();

                        }
                    });


}

