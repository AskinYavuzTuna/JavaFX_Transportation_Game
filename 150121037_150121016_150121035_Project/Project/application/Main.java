package application;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.ArrayList;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.shape.Polyline;

public class Main extends Application {
	//In this part, the instance variables that we will use are defined.
	public static int level =0;
	public ArrayList<City> cities=new ArrayList<City>();
	public Vehicle vehicle;
	public Polyline polyline=new Polyline();
	public ObservableList<Double> list = polyline.getPoints();
	public Circle vehicleimage;
	public boolean fullscreenMod;
	public int number;
	public int score;
	public int currentPassenger;
	public Button drive = new Button("DRIVE");
	public Button nextLevel = new Button("Next Level>>");
	public boolean ender=false;
	@Override
	public void start(Stage primaryStage) {
		//In this part, the screen to open the game and the start message are created.
		BorderPane pane = new BorderPane();
		BorderPane top = new BorderPane();
		Pane center = new Pane();
		BorderPane bottom = new BorderPane();
		FlowPane bottomLeft = new FlowPane();
		ScrollPane scroll = new ScrollPane(bottomLeft);
		scroll.setPrefSize(350, 167);
		bottomLeft.setPadding(new Insets(2,2,2,2));
		bottomLeft.setMaxHeight(167);
        bottom.setMaxHeight(167);
		bottomLeft.setAlignment(Pos.TOP_LEFT);		
		bottomLeft.setOrientation(Orientation.VERTICAL);
		Text information = new Text("Welcome to the Travel!\n Please click the next level to start the game.");
		
		bottomLeft.getChildren().add(information);
		
		//Added background color to game screen.
		top.setStyle("-fx-border-color: black");
		bottom.setStyle("-fx-border-color: black");
		center.setStyle("-fx-border-color: black");
		File wallpaper = new File("levels\\background.jpg");
		BackgroundImage background= new BackgroundImage(new Image(wallpaper.getAbsolutePath(),200,200,false,true),BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,BackgroundSize.DEFAULT);
		center.setBackground(new Background(background));
		
		//A section showing the level at the top left has been added.
		StackPane topLeft = new StackPane();
		topLeft.getChildren().add(new Text("Level: " + level));
		topLeft.setPadding(new Insets(2,2,2,2));
		
		StackPane bottomRight = new StackPane();
		StackPane topRight = new StackPane();
		
		/*When the level is finished, a button has been created that clears the information and pictures of that level,
		   updates the level information and moves to the new level.*/
        class LevelHandler implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent arg0) {
            	level++;
            	currentPassenger=0;
            	bottomLeft.getChildren().clear();
            	topLeft.getChildren().clear();
            	topLeft.getChildren().add(new Text("Level: " + level));
            	center.getChildren().clear();
            	levelInitializer("level"+level+".txt",pane,center,bottomLeft,bottomRight,topRight);
            	bottomRight.getChildren().clear();
            }
        }
        nextLevel.setStyle("-fx-cursor: hand;");
        nextLevel.setOnAction(new LevelHandler());
        topRight.getChildren().add(nextLevel);
		
        //The part where the score information will appear has been created
		StackPane topMid = new StackPane();
		topMid.getChildren().add(new Text("Score:"+score));
		
			
		/*The drive button was created and placed at the bottom left and the background color was changed.
		In this way, it became the same color as the pane it was on. Only the DRIVE text has been highlighted.*/
		bottomRight.setAlignment(Pos.CENTER_RIGHT);
		Font font = new Font(30);
		drive.setFont(font);
		drive.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");
		
		
		
        /*This part opens the level to be played. It prints information about the clicked cities,
           creates a road between the two and moves the vehicle from city to city.
           As a result, it reduces the number of passengers and calculates the score.
            If there is no more level to play, it will print the ending message. */         
        class DriveHandler implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent arg0) {
            	
            	PathTransition pt = new PathTransition();
            	pt.setDuration(Duration.millis(4000));
            	pt.setPath(polyline);
            	pt.setNode(vehicleimage);
            			pt.play();
            			double distance=distance(cities.get(number).coordinate,cities.get(vehicle.currentCityId).coordinate);
            			for(int j=0;j<cities.get(number).arrivals.size();j++) {
                			if(cities.get(number).arrivals.get(j).startingCityId==vehicle.currentCityId && cities.get(number).arrivals.get(j).numberOfPassenger != 0) {
                				if(cities.get(number).arrivals.get(j).numberOfPassenger>vehicle.passengerCapacity) {
                					cities.get(number).arrivals.get(j).numberOfPassenger-=vehicle.passengerCapacity;
                					currentPassenger-=vehicle.passengerCapacity;
                					score+=vehicle.passengerCapacity*0.2*distance;
                					break;
                				}
                				else {
                					score+=cities.get(number).arrivals.get(j).numberOfPassenger*0.2*distance;
        							currentPassenger-=cities.get(number).arrivals.get(j).numberOfPassenger;
                					cities.get(number).arrivals.get(j).numberOfPassenger=0;
                					for(int i = 0; i < cities.get(vehicle.currentCityId).passengers.size(); i++) {
                						if(cities.get(vehicle.currentCityId).passengers.get(i).numberOfPassenger == cities.get(number).arrivals.get(j).numberOfPassenger) {
                							cities.get(vehicle.currentCityId).passengers.get(i).numberOfPassenger=0;
                							break;
                						}
                					}
                					break;
                				}
                			}
            			}
            			score-=distance;
            			topMid.getChildren().clear();
            			topMid.getChildren().add(new Text("Score:"+score));
            			vehicle.currentCityId=number;
            			bottomRight.getChildren().clear();
            			if(currentPassenger==0) {
            				topRight.getChildren().clear();
            				topRight.getChildren().add(nextLevel);
            			}
        				if(currentPassenger == 0 && ender) {
        					bottomLeft.getChildren().clear();
        					levelInitializer("level"+0+".txt",pane,center,bottomLeft,bottomRight,topRight);
        					Text over = new Text("Game Over. Thanks for Playing!");
        					over.setFont(new Font(20));
        					bottomLeft.getChildren().add(over);
        				}
            }
        }
        drive.setOnAction(new DriveHandler());
		
		bottomRight.setPadding(new Insets(50,10,50,10));
		
		bottomRight.setMaxHeight(167);
		
		
		/*It adds the small panels we have created to the grid pane,
		 *  which is the main screen, to the written position.
		 */
		top.setLeft(topLeft);
		top.setRight(topRight);
		top.setCenter(topMid);
		bottom.setRight(bottomRight);
		bottom.setLeft(scroll);
		pane.setTop(top);
		pane.setCenter(center);
		pane.setBottom(bottom);
		
		
		/*When the game is run, it first opens on a small screen,
		   while a code block has been written that allows the user to make it full screen if he/she wants.
		 */
		FlowPane menu = new FlowPane();
		menu.setVgap(30);
		menu.setPadding(new Insets(30,0,30,0));
		menu.setOrientation(Orientation.VERTICAL);
		menu.setAlignment(Pos.CENTER);
		CheckBox fullscreen = new CheckBox("FULLSCREEN");
		fullscreen.setStyle("-fx-border-width: 0; -fx-cursor: hand;");
		class Fullscreen implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent arg0) {
            	if(fullscreen.isSelected()==true)
            		fullscreenMod=true;
            	else
            		fullscreenMod=false;
            }
        }
		
		//piece of code that sets the screen size.
		fullscreen.setOnAction(new Fullscreen());
		Scene scene = new Scene(pane,600,790);
		Scene main = new Scene(menu,300,150);
		
		
		//Added start button that starts the game. The size and background color have been changed.
		Button start = new Button("START");
		start.setTranslateX(20);
		start.setStyle("-fx-border-width: 0; -fx-cursor: hand;");
		class Start implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent arg0) {
            	primaryStage.setScene(scene); 
            	primaryStage.setResizable(false);
            	if(fullscreenMod) {
            		primaryStage.setMaximized(fullscreenMod);
            	}
            }
        }
		start.setOnAction(new Start());
		
		menu.getChildren().add(fullscreen);
		menu.getChildren().add(start);
		
		
		//Added game name and logo.
		primaryStage.setTitle("Travel");
		File file = new File("levels\\icon.jpg");
		primaryStage.getIcons().add(new Image(file.getAbsolutePath()));
		primaryStage.setScene(main);
		primaryStage.show();	
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	/*This method is used when creating a city. Creates clickable city buttons by taking random images from the levels folder.
	  It puts these pictures in cities. It also adds the city names under the created cities.
	 */
	public void addCity1(String cityName,int x,Pane pane,Pane center, FlowPane bottomLeft,StackPane bottomRight){
		int o=x;
		x=x-1;
		int a=x/10;
		int b=x%10;
		File file = new File("levels\\city"+(int)(Math.random()*6+1)+".jpg");
		Circle c1 = createCircle(30+60*b,30+60*a, 30, file.getAbsolutePath());
		c1.radiusProperty().bind((center.heightProperty()).divide(20));
		c1.centerXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(c1.radiusProperty()).add((pane.widthProperty().divide(2))));
		c1.centerYProperty().bind ((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()));
		center.getChildren().add(c1);
		
		Font font1=new Font(19);
		Text text=new Text(cityName);
		text.setFont(font1);
		text.xProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(pane.widthProperty().divide(2)));
		text.yProperty().bind ((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty().multiply(2.65)));
		center.getChildren().add(text);
		
		Button deneme = new Button("   ");
		Font asd = new Font(30);
		deneme.setStyle("-fx-background-color: transparent; -fx-border-width: 0; -fx-cursor: hand;");
		deneme.setFont(asd);
		center.getChildren().add(deneme);
		deneme.setLayoutX(450);
		deneme.setLayoutY(300);
        Circle buttonCircle = new Circle(60);
        buttonCircle.setStyle("-fx-cursor: hand;");
        buttonCircle.radiusProperty().bind((pane.heightProperty().subtract(190)).divide(20));
		deneme.setShape(buttonCircle);
		deneme.layoutXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(c1.radiusProperty()).add((pane.widthProperty().divide(2)).subtract(32)));
		deneme.layoutYProperty().bind ((center.heightProperty()).divide(10).multiply(a).subtract(33).add(c1.radiusProperty()));
		
		
		class Deneme implements EventHandler<ActionEvent> {
            @Override
            public void handle(ActionEvent arg0) {
            	
            	for(int a=0;a<cities.size();a++) {
            		if(cities.get(a)!=null)
            		if(cities.get(a).coordinate==o) {
            			getInfo(cities.get(a), bottomLeft);
            			addline(pane,center,cities.get(a).cityNumber,bottomRight);
            			number=cities.get(a).cityNumber;
            		}
            	}
            }
        }
		deneme.setOnAction(new Deneme());		
	}
	//This method adds parts that should not be navigated to the screen. These parts appear as circles with crosses in them.
	public void addFixed(int x,Pane pane,Pane center){
		x=x-1;
		int a=x/10;
		int b=x%10;
		Circle c1 =new Circle(30+60*b,53+60*a, 30);
		c1.setStroke(Color.RED);
		c1.setFill(Color.WHITE);
		c1.radiusProperty().bind((center.heightProperty()).divide(20));
		c1.centerXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(c1.radiusProperty()).add((pane.widthProperty().divide(2))));
		c1.centerYProperty().bind ((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()));
		center.getChildren().add(c1);
		Line line1 = new Line();
		center.getChildren().add(line1);
		line1.startXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(300).add(c1.radiusProperty()).add((pane.widthProperty().divide(2)).subtract(300)).subtract(c1.radiusProperty().divide(3)));
		line1.startYProperty().bind((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()).subtract(c1.radiusProperty().divide(3)));
		line1.endXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(300).add(c1.radiusProperty()).add((pane.widthProperty().divide(2)).subtract(300)).add(c1.radiusProperty().divide(3)));
		line1.endYProperty().bind((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()).add(c1.radiusProperty().divide(3)));
		
		Line line2 = new Line();
		center.getChildren().add(line2);
		line2.startXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(c1.radiusProperty()).add((pane.widthProperty().divide(2))).subtract(c1.radiusProperty().divide(3)));
		line2.startYProperty().bind((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()).add(c1.radiusProperty().divide(3)));
		line2.endXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(c1.radiusProperty()).add((pane.widthProperty().divide(2))).add(c1.radiusProperty().divide(3)));
		line2.endYProperty().bind((center.heightProperty()).divide(10).multiply(a).add(c1.radiusProperty()).subtract(c1.radiusProperty().divide(3)));
		
		
	}
	
	/*this method creates a vehicle. Selects and uses the appropriate picture according to the level and vehicle capacity.
	   The vehicle appears above the city picture and goes to the selected city.
	  In this way, the user can understand more easily between which cities they carry passengers.
	 */
	public void addVehicle(int cityId,Pane pane,Pane center,int capacity) {
		int x=0;
		for(int k=0;k<cities.size();k++) {
			if(cities.get(k)!=null) {
				if(cities.get(k).cityNumber==cityId)
					x=cities.get(k).coordinate;
			}		
		}
		x=x-1;
		int a=x/10;
		int b=x%10;
		File file;
		if(capacity<6)
			file = new File("levels\\vehicle1.png");
		else if(capacity<14)
			file = new File("levels\\vehicle2.png");
		else
			file = new File("levels\\vehicle3.png");
		vehicleimage = createCircle(30+60*b,30+60*a, 20, file.getAbsolutePath());
		vehicleimage.radiusProperty().bind((center.heightProperty()).divide(15));
		vehicleimage.centerXProperty().bind((center.heightProperty()).divide(10).multiply(b-5).add(vehicleimage.radiusProperty()).add((pane.widthProperty().divide(2))));
		vehicleimage.centerYProperty().bind ((center.heightProperty()).divide(10).multiply(a).add(vehicleimage.radiusProperty()));
		
			
		center.getChildren().add(vehicleimage);
		
	}
	
	//It allows to get the necessary information from the txt file in the folder and initializes the level according to input file.
	public void levelInitializer(String filename,Pane pane,Pane center,FlowPane bottomLeft,StackPane bottomRight,StackPane topRight) {
		try {
			center.getChildren().clear();
			cities.clear();
			list.clear();
			list.add(0.0);
			list.add(0.0);
			topRight.getChildren().clear();
			score=0;
			center.getChildren().add(polyline);
			
			
			java.io.File file = new java.io.File("levels\\"+filename);
			java.io.File existance = new java.io.File("levels\\"+"level"+(level+1)+".txt");
				
			Scanner scanner = new Scanner(file);
			while(scanner.hasNextLine()) {
				String item = scanner.nextLine();
				addItem(item,pane,center,bottomLeft,bottomRight);
			}
			if(!existance.exists()) {
				ender=true;
				nextLevel = new Button("Last Level");
				nextLevel.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");

			}
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}		
		topRight.getChildren().clear();
		
	}
	
	/*This part looks at which letter the line is connected with in the txt file.
	   It checks the necessary conditions with the switch block and creates the city, passenger, vehicle and fixed information.
	 */
	public void addItem(String item,Pane pane,Pane center,FlowPane bottomLeft,StackPane bottomRight) {
		int first,second,third;
		switch (item.charAt(0)) {
			case 'C' : 	first = item.indexOf(',');
						second = item.indexOf(',', first+1);
						third = item.indexOf(',', second+1);
						addCity1(item.substring(first+1,second),Integer.parseInt(item.substring(second+1, third)),pane,center,bottomLeft,bottomRight);
						City city = new City(item.substring(first+1, second), Integer.parseInt(item.substring(second+1, third)), Integer.parseInt(item.substring(third+1)));
						if (cities.size()<Integer.parseInt(item.substring(third+1))){
							for(int a=Integer.parseInt(item.substring(third+1));a>cities.size();){
								cities.add(null);
							}
						}
						cities.add(Integer.parseInt(item.substring(third+1)), city);
						if(cities.size()>Integer.parseInt(item.substring(third+1))+1)
							cities.remove(Integer.parseInt(item.substring(third+1))+1);
						break;
			case 'P' :	first = item.indexOf(',');
						second = item.indexOf(',', first+1);
						third = item.lastIndexOf(',');
						City currentCity1=cities.get(Integer.parseInt(item.substring(second+1, third)));
						City currentCity2=cities.get(Integer.parseInt(item.substring(third+1)));
						Passenger passenger = new Passenger(Integer.parseInt(item.substring(first+1, second)), Integer.parseInt(item.substring(second+1, third)), Integer.parseInt(item.substring(third+1)),currentCity1.cityName,currentCity2.cityName);
						for(int a=0;a<cities.size();a++) {
							if(cities.get(a)!=null) {
								if(cities.get(a).cityNumber==Integer.parseInt(item.substring(second+1, third)))
									cities.get(a).passengers.add(passenger);
								if(cities.get(a).cityNumber==Integer.parseInt(item.substring(third+1)))
									cities.get(a).arrivals.add(passenger);
							}
						}
						currentPassenger+=(Integer.parseInt(item.substring(first+1, second)));
						break;
			case 'V' :	first = item.indexOf(',');
						second = item.indexOf(',', first+1);
						third = item.indexOf(',', second+1);
						vehicle = new Vehicle(Integer.parseInt(item.substring(first+1, second)), Integer.parseInt(item.substring(second+1)));
						addVehicle(cities.get(Integer.parseInt(item.substring(first+1, second))).cityNumber, pane, center, (Integer.parseInt(item.substring(second+1))));
						
						break;
			case 'F' :	first = item.indexOf(',');
							addFixed(Integer.parseInt(item.substring(first+1)),pane,center);
						Fixed fixed = new Fixed(Integer.parseInt(item.substring(first+1)));
						break;
			default  :	break;
		}
	}
	
	//method for creating a circle to put image on it.
	private Circle createCircle(double centerX, double centerY, double radius, String imagePath) {
        Circle circle = new Circle(centerX, centerY, radius);
        circle.setFill(new ImagePattern(new Image(imagePath)));

        return circle;
    }
	
	
	/*This part is the code that provides access to information about the city.
	   Gets the ID, distance and vehicle capacity information of the cities.
	   It gives the information of the number of passengers who will come to that city and will leave from that city.
	 */
	public void getInfo(City city, FlowPane bottomLeft) {
        bottomLeft.getChildren().clear();
        bottomLeft.setMaxHeight(40);
        String info = city.cityName + "(City ID: " + city.cityNumber + "  Distance: " + distance(cities.get(vehicle.currentCityId).coordinate,city.coordinate) + "  Vehicle Capacity: " + vehicle.passengerCapacity + ")\n\n";
        for(int i = 0; i < city.arrivals.size(); i++) {
            if(city.arrivals.get(i).numberOfPassenger>0) {
                bottomLeft.setMaxHeight(bottomLeft.getMaxHeight()+32);
                info += "     " + city.arrivals.get(i).startingCityName + " > " + city.cityName + " ( " +  city.arrivals.get(i).numberOfPassenger + " Passengers)\n";
                Text message = new Text(info);
                bottomLeft.getChildren().add(message);
                info = "";
            }
        }
        for(int i = 0; i < city.passengers.size(); i++) {
            if(city.passengers.get(i).numberOfPassenger>0) {
                bottomLeft.setMaxHeight(bottomLeft.getMaxHeight()+32);
                info += "     " + city.cityName + " > " + city.passengers.get(i).destinationCityName + " ( " +  city.passengers.get(i).numberOfPassenger + " Passengers)\n";
                Text message = new Text(info);
                bottomLeft.getChildren().add(message);
                info = "";
            }
        }

    }
	//piece of code that calculates the distance between cities with their coordinates.
	public int distance(int first,int second) {
		first-=1;
		second-=1;
		int x1=first%10;
		int y1=first/10;
		int x2=second%10;
		int y2=second/10;
		return (int)Math.ceil(Math.sqrt((Math.pow(x2-x1,2)+Math.pow(y2-y1,2))));
		
	}
	/*this code draws a line between the cities selected by the user.
	   The vehicle reaches the other city via this line .
	 */
	public Polyline addline(Pane pane,Pane center,int endingCityId,StackPane bottomRight) {
		Rectangle r1 =new Rectangle(0,0,0,0);
		r1.heightProperty().bind(center.heightProperty().divide(10));
		Rectangle r2 =new Rectangle(0,0,0,0);
		r2.widthProperty().bind(center.widthProperty().divide(10));
		polyline.setStrokeWidth(7);
		polyline.setStroke(Color.GREEN);
		list.clear();
		bottomRight.getChildren().clear();
		bottomRight.getChildren().add(drive);
		int x=0;
		for(int k=0;k<cities.size();k++) {
			if(cities.get(k)!=null) {
				if(cities.get(k).cityNumber==vehicle.currentCityId)
					x=cities.get(k).coordinate;
			}		
		}
		x=x-1;
		int a=x/10;
		int b=x%10;
		list.add((1.0*b-4.5)*r1.getHeight()+r2.getWidth()*5);
		list.add((1.0*a+0.5)*r1.getHeight());
		for(int k=0;k<cities.size();k++) {
			if(cities.get(k)!=null) {
				if(cities.get(k).cityNumber==endingCityId)
					x=cities.get(k).coordinate;
			}		
		}
		x=x-1;
		a=x/10;
		b=x%10;
		list.add((1.0*b-4.5)*r1.getHeight()+r2.getWidth()*5+1);
		list.add((1.0*a+0.5)*r1.getHeight());
	return polyline;	
	}
	
	public void addDirection(String direction,Rectangle height) {
        int a=list.size();
        switch(direction) {

        case "U":

            list.add(a-2,list.get(a-4));
            list.add(a-1,list.get(a-3)-height.getHeight());

            break;

        case "R":

            list.add(a-2,list.get(a-4)+height.getHeight());
            list.add(a-1,list.get(a-3));
            list.get(0);

            break;

        case "D":

            list.add(a-2,list.get(a-4));
            list.add(a-1,list.get(a-3)+height.getHeight());

            break;

        case "L":

            list.add(a-2,list.get(a-4)-height.getHeight());
            list.add(a-1,list.get(a-3));

            break;
        }
    }
}