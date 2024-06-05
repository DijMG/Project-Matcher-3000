package com.example.project_matcher;

import static android.provider.MediaStore.Images.Media.getBitmap;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.gridlayout.widget.GridLayout;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    ArrayList<Integer> TileIds = new ArrayList<>();
    ArrayList<ImageView> Randomizer_Tile_Holder = new ArrayList<>();
    ArrayList<ImageView> Gameplay_Suspended_Tiles = new ArrayList<>();
    ArrayList<Object[]> Gameplay_Last_Clicked_Tiles = new ArrayList<>();
    int Tile_Counter = 0;
    int Click = 0;
    int Time_Value = -1;

    int Found_Pairs = 0;

    boolean Handler_Stop = false;
    public void Game_Enter(View view){
        Found_Pairs = 0;
        Handler_Stop = false;
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText X_Text = findViewById(R.id.Size_X);
        String inputTextX = X_Text.getText().toString();
        int Size_X;

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText Y_Text = findViewById(R.id.Size_Y);
        String inputTextY = Y_Text.getText().toString();
        int Size_Y;

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) EditText Time_Text = findViewById(R.id.Time);
        String Input_Time_Text = Time_Text.getText().toString();
        Time_Value = -1;
        try {
            Size_X = Integer.parseInt(inputTextX);
            Size_Y = Integer.parseInt(inputTextY);
            CheckBox Timer_Switch = findViewById(R.id.Timer_Checkbox);
            if(Timer_Switch.isChecked()){
                Time_Value = Integer.parseInt(Input_Time_Text);

            }
        } catch (NumberFormatException e) {
            return;
        }
        if(Size_X > 9 || Size_Y > 6){
            return;
        }
        if ((Size_X*Size_Y)%2 != 0){
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setMessage("The size doesn't make an even number!");
            builder.setTitle("Uh Oh!");
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            return;
        }
        setContentView(R.layout.game);
        ((TextView) findViewById(R.id.Game_Time)).setText(String.valueOf(Time_Value));
        for(int x = 1; x < 50;x++){
            TileIds.add(x);
        }
        Tile_Counter = 3;
        Click = 0;
        Randomizer_Tile_Holder.clear();
        Gameplay_Last_Clicked_Tiles.clear();
        Gameplay_Suspended_Tiles.clear();
        GridLayout gridLayout = findViewById(R.id.Tile_Map);
        gridLayout.removeAllViews();
        gridLayout.setRowCount(Size_X);
        gridLayout.setColumnCount(Size_Y);

        // Create and add ImageViews to the GridLayout
        for (int i = 0; i < Size_X*Size_Y/2; i++) {
            //int random_INT = new Random().nextInt(50 - 1) + 1;
            final int counter = Tile_Counter;
            for(int x = 0;x <= 1;x++){
                System.out.println("x counter: " + x);
                ImageView imageView = new ImageView(this);
                // Set image resource from drawables
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                imageView.setImageResource(R.drawable.tile);
                imageView.setTag("Tile");
                // Set size of ImageView to 55dp square
                int sizeInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = sizeInDp;
                params.height = sizeInDp;
                imageView.setLayoutParams(params);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override

                    public void onClick(View v) {
                        if (imageView.getTag() == "Tile") {
                            Click += Click + 1;
                            if(Click > 3){
                                Click = 1;
                                Gameplay_Last_Clicked_Tiles.clear();
                            }
                            System.out.println("Click: " + Click);
                            if(Click > 1){
                                for(ImageView item : Gameplay_Suspended_Tiles){
                                    if(item.getTag() != "Completed"){
                                        item.setTag("Tile");
                                        animateImageView(item,R.drawable.tile,0);
                                    }
                                }
                                Gameplay_Suspended_Tiles.clear();
                            }
                            imageView.setTag("Not_Tile");
                            Gameplay_Last_Clicked_Tiles.add(new Object[]{imageView, counter});
                            if(Gameplay_Last_Clicked_Tiles.size() > 2){
                                Gameplay_Last_Clicked_Tiles.remove(2);
                            }
                            if(Gameplay_Last_Clicked_Tiles.size() == 2) {
                                Object[] objArray = Gameplay_Last_Clicked_Tiles.get(0);
                                Object[] objArray2 = Gameplay_Last_Clicked_Tiles.get(1);
                                if ((int) objArray[1] == (int) objArray2[1]) {
                                    Found_Pairs += 1;
                                    ((ImageView) objArray[0]).setTag("Completed");
                                    animateImageView(((ImageView) objArray[0]), R.drawable.tile, counter);
                                    ((ImageView) objArray2[0]).setTag("Completed");
                                    animateImageView(((ImageView) objArray2[0]), R.drawable.tile, counter);
                                    //((ImageView) objArray[0]).setVisibility(view.INVISIBLE);
                                    //((ImageView) objArray2[0]).setVisibility(view.INVISIBLE);
                                }
                            }
                            if(imageView.getTag() != "Completed"){
                                animateImageView(imageView,R.drawable.tile,counter);
                            }
                        }
                    }
                });
                Randomizer_Tile_Holder.add(imageView);
            }
            System.out.println("Tile counter: " + Tile_Counter);
            Tile_Counter = Tile_Counter + 1;
        }
        final int Randomizer_Size = Randomizer_Tile_Holder.size();
        for(int x = 0;x < Randomizer_Size;x++){
            int random_INT = new Random().nextInt(Randomizer_Size - x);
            System.out.println("random_index: " + random_INT);
            gridLayout.addView(Randomizer_Tile_Holder.get(random_INT));
            Randomizer_Tile_Holder.remove(random_INT);
            System.out.println("length: " + Randomizer_Size);
            System.out.println("random spawn counter: " + x);
        }
        System.out.println("test0: " + Time_Value);
        if(Time_Value > -1) {
            System.out.println("test0: " + Time_Value);
            Handler handler = new Handler();
            handler.postDelayed(runnable, 1000);

        }
    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            ((ImageView) findViewById(R.id.Game_Visual_Time)).requestLayout();
            String Time_Text = ((TextView) findViewById(R.id.Game_Time)).getText().toString();
            int Incremented_value = Integer.parseInt(Time_Text) - 1;
            System.out.println(Found_Pairs + "," + Tile_Counter);
            ((ImageView) findViewById(R.id.Game_Visual_Time)).getLayoutParams().width = Math.round((1123f * ((float)Incremented_value/(float)Time_Value))); // Change the width as needed
            ((TextView) findViewById(R.id.Game_Time)).setText(String.valueOf(Incremented_value));
            if (Found_Pairs + 3 == Tile_Counter){
                setContentView(R.layout.activity_main);
                Randomizer_Tile_Holder.clear();
                Gameplay_Last_Clicked_Tiles.clear();
                Gameplay_Suspended_Tiles.clear();
                Handler_Stop = true;
                return;
            }else if(Incremented_value == 0){
                setContentView(R.layout.activity_main);
                ((ImageView) findViewById(R.id.Background)).setImageResource(R.drawable.explosion);
                // The crashing Is a feature v
            }
            Handler handler = new Handler();
            handler.postDelayed(this, 1000);
        }
    };
    private void animateImageView(ImageView imageview, int resourceId, int Counter) {
        if (imageview.getTag() =="Tile"){
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageview, View.ROTATION_Y, 0f, -180f);
            animator.setDuration(500);


            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getCurrentPlayTime() >= animation.getDuration() / 2) {
                        imageview.setImageResource(R.drawable.tile);
                    }
                }
            });
            animator.start();
        }
        else if(imageview.getTag() =="Not_Tile"){
            // Create an ObjectAnimator to rotate the ImageView around the Y axis
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageview, View.ROTATION_Y, 0f, 180f);
            animator.setDuration(500);
            Gameplay_Suspended_Tiles.add(imageview);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getCurrentPlayTime() >= animation.getDuration() / 2) {
                        imageview.setImageResource(R.drawable.tile+Counter);
                    }
                    if(animation.getCurrentPlayTime() >= animation.getDuration()){
                        //imageview.setTag("Tile");

                        if(Click == 2){
                            //animateImageView(imageview,R.drawable.tile,Counter);
                        }
                    }
                }
            });
            animator.start();

        }
        else if(imageview.getTag() =="Completed") {
            ObjectAnimator animator = ObjectAnimator.ofFloat(imageview, View.ROTATION_Y, 0f, 180f);
            animator.setDuration(500);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (animation.getCurrentPlayTime() >= animation.getDuration() / 2) {
                        imageview.setImageResource(R.drawable.checked);
                    }
                }
            });
            animator.start();
        }


    }

    public void Game_Exit(View view){
        setContentView(R.layout.activity_main);
    }

    public void Time_Switch_Click(View v) {
        CheckBox Timer_Switch = findViewById(R.id.Timer_Checkbox);
        if(Timer_Switch.isChecked()){
            findViewById(R.id.Time).setEnabled(true);
        }else{
            findViewById(R.id.Time).setEnabled(false);
        }
    }
}