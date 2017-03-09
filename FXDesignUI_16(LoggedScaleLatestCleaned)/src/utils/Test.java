package utils;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.ArcType;
import javafx.stage.Stage;
import materialdesignbutton.MaterialDesignButtonWidget.VAL;
import status.three.Status3Widget;

public class Test extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        /*********************
         * EXAMPLE BUTTON
         ********************/
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        /***********************
         * CANVAS
         **********************/
        Canvas canvas = new Canvas(800, 300);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.fillRect(0, 0, 10, 10);
        Stop[] stops = new Stop[] { new Stop(0, Color.RED),  new Stop(1, Color.GREENYELLOW)};
	    LinearGradient linearGradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
	    gc.setFill(linearGradient);
//	    gc.rect( 0, 0, 300, 300);
	    gc.fill();
//	    gc.setFill(Color.GREEN);
	    for(double i=-10;i<10;i+=0.5)
	    	gc.fillArc(0, 0, 800, 300, i, i+0.5, ArcType.ROUND);

	    //draw diamond shade
	    double x = 100;
	    double y = 100;
	    int size = 10;
	    gc.fillPolygon(new double[]{x, x-size, x, x+size},
                new double[]{y-size, y, y+size, y}, 4);


	    /*****************************
	     * Status Three Widget
	     ****************************/
	    Status3Widget status3Widget1 = new Status3Widget();
	    status3Widget1.widgetSetName(" 1");
	    status3Widget1.widgetSetNameVal_1("CH1");
	    status3Widget1.widgetSetNameVal_2("CH2");
	    status3Widget1.widgetSetNameVal_3("TEMP");
	    status3Widget1.widgetSetNameVal_4("+28");
	    status3Widget1.widgetSetNameVal_5("-5");
	    status3Widget1.widgetSetNameVal_6("+8");
	    status3Widget1.widgetSetNameVal_7("+5");

	    status3Widget1.widgetSetVal_1(VAL.RED);
	    status3Widget1.widgetSetVal_2(VAL.YELLOW);
	    status3Widget1.widgetSetVal_3(VAL.GREEN);
	    status3Widget1.widgetSetVal_4(VAL.RED);
	    status3Widget1.widgetSetVal_5(VAL.YELLOW);
	    status3Widget1.widgetSetVal_6(VAL.GREEN);
	    status3Widget1.widgetSetVal_7(VAL.GREEN);

	    Status3Widget status3Widget2 = new Status3Widget();
	    status3Widget2.widgetSetName(" 2");
	    status3Widget2.widgetSetNameVal_1("CH1");
	    status3Widget2.widgetSetNameVal_2("CH2");
	    status3Widget2.widgetSetNameVal_3("TEMP");
	    status3Widget2.widgetSetNameVal_4("+28");
	    status3Widget2.widgetSetNameVal_5("-5");
	    status3Widget2.widgetSetNameVal_6("+8");
	    status3Widget2.widgetSetNameVal_7("+5");

	    status3Widget2.widgetSetVal_1(VAL.DEFAULT);
	    status3Widget2.widgetSetVal_2(VAL.YELLOW);
	    status3Widget2.widgetSetVal_3(VAL.GREEN);
	    status3Widget2.widgetSetVal_4(VAL.DEFAULT);
	    status3Widget2.widgetSetVal_5(VAL.YELLOW);
	    status3Widget2.widgetSetVal_6(VAL.GREEN);
	    status3Widget2.widgetSetVal_7(VAL.GREEN);


        StackPane root = new StackPane();
        VBox vBox = new VBox();
        vBox.getChildren().add(btn);
        vBox.getChildren().add(status3Widget1);
        vBox.getChildren().add(status3Widget2);
        root.getChildren().add(vBox);
        root.getChildren().add(canvas);
        primaryStage.setScene(new Scene(root, 800, 500));
        primaryStage.show();
        
        /*************
         * 
         ***********/
       // String myVal = "hello wrld";
       // String[] mySpl = myVal.split(" ");       // These lines are commented because of no where it is used.
        System.out.println(AppUtils.fixedLengthString("abcdefghi", 30));
        System.out.println(AppUtils.fixedLengthString("abc", 30));
    }

	public void runTest(Canvas canvas) {

        //TESTING
        long startTime = System.currentTimeMillis();
        //drawing all pixel with Rect size 1x1
        Test.gcDrawEveryPixelRect(canvas);//470ms-528ms
        Test.g2dImgDrawEveryPixelRect(canvas);//about 201ms
        //drawing/filling single Rect size 100x100
        Test.gcDrawSingleRect(canvas);//about 5ms
        Test.g2dImgDrawSingleRect(canvas);//about 51ms
        long endTime = System.currentTimeMillis();
        System.out.println("TotalTime: "+(endTime - startTime));
	}


	public static void gcDrawSingleRect(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		 //single Rect
		gc.setFill(Color.RED);
		gc.strokeRect(100, 100, 100, 100);
	}

	public static void gcDrawEveryPixelRect(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();

		//Every pixel
		Random r = new Random();
		for(int i = 0; i<=canvas.getWidth(); i++) {
            for(int j = 0; j<=canvas.getHeight(); j++) {
                if (r.nextBoolean()) {
                    gc.setFill(Color.RED);
                } else {
                    gc.setFill(Color.GREEN);
                }
                gc.fillRect(i, j, 1, 1);
            }
        }
	}

	public static void g2dImgDrawSingleRect(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.save();
		gc.drawImage(drawPixel(canvas,true),0,0);
		gc.restore();
	}

	public static void g2dImgDrawEveryPixelRect(Canvas canvas) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.save();
		gc.drawImage(drawPixel(canvas,false),0,0);
		gc.restore();
	}

	private static Image drawPixel (Canvas canvas, boolean isSingle) {
		 BufferedImage bufferedImage = new BufferedImage((int) (canvas.getWidth()),
				 (int) canvas.getHeight(), BufferedImage.TYPE_INT_RGB);
		 Graphics2D g2d = bufferedImage.createGraphics();

		 if(isSingle) {
			 //single Rect
			 g2d.setColor(java.awt.Color.red);
			 g2d.drawRect(100, 100, 100, 100);
		 } else {
			//Every pixel
			 Random r = new Random();
			 for(int i = 0; i<=bufferedImage.getWidth(); i++) {
		            for(int j = 0; j<=bufferedImage.getHeight(); j++) {
		                if (r.nextBoolean())
		                	g2d.setColor(java.awt.Color.red);
		                else
		                	g2d.setColor(java.awt.Color.green);
		                g2d.drawRect(i, j, 1, 1);
		            }
			 }
		 }

		 WritableImage wr = null;
		 Image img = SwingFXUtils.toFXImage(bufferedImage, wr);
		 return img;

		 // start working block
//		 WritableImage wr = null;
//		 if (bufferedImage != null) {
//			 wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
//			 PixelWriter pw = wr.getPixelWriter();
//			 for (int x = 0; x < bufferedImage.getWidth(); x++) {
//				 for (int y = 0; y < bufferedImage.getHeight(); y++) {
//					 if (r.nextBoolean()) {
//						 pw.setColor(x,y, Color.RED);
//		                } else {
//		                	pw.setColor(x,y, Color.BLUE);
//		                }
//
//					 }
//				 }
//			 }
//		 return (Image)wr;
	 }

}
