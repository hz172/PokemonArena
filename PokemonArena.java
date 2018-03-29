/*
 * Helen Zhang
 * PokemonArena.java
 *
 * The main organization portion of the program, especially those related solely to the variables listed in this class.
 * Also contains most of the User Interface, whereas the battle logic is in the Pokemon class.
 *
 */


import java.io.*;
import java.util.*;
public class PokemonArena{
	
	private static Pokemon current;							// variables and more variables
	private static Pokemon enemy;
	
	private static ArrayList<Pokemon>team=new ArrayList<Pokemon>();
	private static ArrayList<Pokemon>enemies=new ArrayList<Pokemon>();
	private static ArrayList<Pokemon>allPoke = new ArrayList<Pokemon>();
	
	private static Random dice=new Random();
	private static Scanner kb=new Scanner(System.in);
	
	private static int turn;	
	
	/*  main loop containing the main structure of the program, making calls to functions/methods for more detailed work
	 *
	 */
	public static void main(String[]args){					
		fileIO();											 
		intro();
		teamAssembly();										// file, introduction, Player choosing team members, putting the rest in enemies
		enemiesAssembly();				
		
		System.out.println("\n\n\nTIME TO BATTLE!");		// the battle begins!
		turn=dice.nextInt(2);							// use this to randomly determine who goes first
		choosePoke();										// players chooses a Pokemon to battle with
		compChoosePoke();									// computer chooses randomly
		statDisp();											// show stats before battle
		while(team.size()>0 || enemies.size()>0){			// while the teams still have members...
			if(turn==0){
				playerAction();								// random turn - player first
				opponentAction();
				
			}
			else if(turn==1){
				opponentAction();							// random turn - computer first				
				playerAction();
			}
			roundOver();									// after the player and opponent have both played their turns, call
															// roundOver() for energy recovery
		}																
	}														
															
	
		
		
		
		
	
	
	// SUPPORT FUNCTIONS
	// all of which are void and all but one of which have no parameters
	
	// read in file, try and catch a possible exception
	// go through file and add Pokemon to allPoke
	private static void fileIO(){						
		Scanner infile = null;
    	try{												 
    		infile = new Scanner(new File("pokemon.txt"));
    	}
    	catch(IOException ex){
    		System.out.println(ex);
    	}
    	int n;
    	n = Integer.parseInt(infile.nextLine());
    	
    	for (int i = 0; i < n; i ++){						 
    		Pokemon p = new Pokemon(infile.nextLine());
    		allPoke.add(p);    		
    	}
	}
		
	private static void intro(){
		System.out.println("Brought to you by The Pulse.");		// honorary introduction
		System.out.println("\nHello! Welcome to POKEMON ARENA!\n");
		System.out.println("Here, you will become a Trainer and battle againsts opponents for a chance ");
		System.out.println("to be crowned TRAINER SUPREME!\n");
		System.out.println("No need to catch 'em all - If you wanna be the very best (like no one ever was),");
		System.out.println("let's get started!\n");		
	}
	
	// allow player to choose Pokemon from a list to use in their team
	private static void teamAssembly(){								
		System.out.println("\nASSEMBLE THE TEAM! (Choose 4)\n");
		System.out.println(" #  Name        HP   Type");
		System.out.println("---------------------------------");
		
		
		for(int i=0;i<allPoke.size();i++){
			(allPoke.get(i)).menu(i+1);								// this is where menu() is called
		}
		
		while(team.size()<4){										// while and if statements to make sure no Pokemon is added twice
			int pok;
			do{
				System.out.println("\nEnter a number: ");
				pok = Integer.parseInt(kb.nextLine());
				if(pok<=allPoke.size() && (!team.contains(allPoke.get(pok-1)))){	// all entering number choices have do while loops to
					team.add(allPoke.get(pok-1));									// prevent invalid input
				}
			}while(pok<0 || pok>allPoke.size());
		}
		
		System.out.println("\n\nTEAM ASSEMBLED!\n");
		System.out.println("Your team includes... ");
		for(int i=0;i<4;i++){										// some player feedback
			System.out.print(team.get(i).getName()+"! ");
		}
	}
	
	// put together enemy team by adding what's not in team from allPoke to enemies
	private static void enemiesAssembly(){		
		for(Pokemon i : allPoke){
			if(!team.contains(i)){
				enemies.add(i);
			}
		}
	}
	
	// recovers energy to both teams, as well as un-stuns them
	private static void roundOver(){			
		for(Pokemon i : team){					// loop for player to get to all team members
			i.roundOverEnergyStun();
		}										// this is where roundOverEnergy() is called
		enemy.roundOverEnergyStun();
		System.out.println("Round over! All Pokemon receive +10 energy!");
		System.out.println("All stun reset!\n");
		checkFaint();
	}
	
	// same as roundOver() but without the stun
	// show remaining number of opponents
	// reset the turn number
	private static void battleOver(){			
		for(Pokemon i : team){
			i.battleOverHpEnergy();
		}
		System.out.println("\nBattle over! All Player's Pokemon receive +20 hp!");
		System.out.println("All energy reset to 50!");
		System.out.println("\nRemaining Opponents: "+enemies.size()+"\n");
		turn=dice.nextInt(2);
	}
	
	// checks for fainted Pokemon and acts accordingly
	// add fainted pokemon to the fainted list, remove the fainted from team, perform fainted-pokemon-related actions
	// then the rest of the functions/methods can be called
	private static void checkFaint(){							
		ArrayList<Pokemon>fainted = new ArrayList<Pokemon>();	// list of fainted Pokemon to avoid the 'you-can't-edit-something-
		boolean faint=false;									// then-iterate-it-again error whose name I can't remember
		for(Pokemon i : team){									// boolean for the same purpose
			if(i.getHp()==0){			
				fainted.add(i);									
			}
		}
		for(Pokemon j:fainted){
			if(team.contains(j)){								// remove the fainted from the team aka edit the list before iterating again to 
				team.remove(j);									// avoid the error
				System.out.println(j.getName()+" has fainted!");
				faint=true;									
			}
		}
		if(team.size()>0 && faint==true){						
			battleOver();										// add hp to the team in battleOver()
			choosePoke();										// choose a new Pokemon to use
			statDisp();											// show the stats
		}
		if(team.size()==0){										// if, after checking for fainted and removing them from the team, the
			System.out.println("All of your Pokemon have fainted! You lose! Better luck next time!\n"); // team list is empty,
			System.exit(1);										// the Pokemon have all fainted and the Player loses.
		}														// exit the program to prevent awkwardly trying to continue

		if(enemy.getHp()==0){
			System.out.println("\nThe opposing "+enemy.getName()+" has fainted!");
			enemies.remove(enemy);								// enemy version of above
			battleOver();										// same process as the Player, however only one opponent can faint at a
			if(enemies.size()>0){								// time, therefore there is no error
				compChoosePoke();
				statDisp();
			}
			else{
				System.out.println("Congratulations! You have defeated all the opposing Pokemon!");
				System.out.println("You are hereby crowned TRAINER SUPREME!\n");
				System.exit(1);									// if the enemies list is empty, the Player has defeated them all and
			}													// wins the game
		}														// once again exit the program to prevent awkwardly trying to continue
	}
	
	// display the stats by calling stat menu() from the Pokemon class
	private static void statDisp(){
		System.out.println("\n*******************************************");
		System.out.println("CURRENT STATUS!\n");								
		System.out.println(" Name        HP   Energy");
		System.out.println("---------------------------------------");
		current.statMenu();
		enemy.statMenu();
		System.out.println("*******************************************\n");	// fancy border because aesthetics
		checkFaint();
	}
	
	// choose a new Pokemon
	// or if they are stunned, inform them that they cannot retreat
	private static void retreat(){
		if(current.getStun()==false){
			System.out.println("\nRETREAT!!"); 			
			choosePoke();
		}
		else{
			System.out.println(current.getName()+" has been stunned! It cannnot retreat!");
		}
		
	}
	
	// when it's the opponents turn, print some feedback and call randomAttack, since the enemy can't retreat and attack will handle passing
	private static void opponentAction(){			
		System.out.println("Opponent's Turn!");		 
		enemy.randomAttack(current);
		statDisp();											// stats are good
	}
	
	// allow the Player to choose their action from a list by entering a number		
	private static void playerAction(){						
		System.out.println("Player's Turn!");
		int actionNum;
		do{
			System.out.println("\nWhat do you want to do?");
			System.out.println(" 1. Attack");
			System.out.println(" 2. Retreat");
			System.out.println(" 3. Pass");
			System.out.println("\nEnter a number: ");
			actionNum=Integer.parseInt(kb.nextLine());
		}while(actionNum<0 || actionNum>3);
		
		chooseAction(actionNum);							// call chooseAction to proccess said action
		statDisp();											// and show some more stats
	}
	
	// calls functions/methods according to what action the Player chose in playerAction()
	private static void chooseAction(int action){
		if(action==1){
			current.chooseAttack(enemy);					
		}
		else if(action==2){
			retreat();
		}
		else if(action==3){
			System.out.println("\nYou have passed this turn.");		// just a print statement if they pass since there's nothing to be done
		}
	}	
	
	// choose a Pokemon from the Player's team to use in battle from a menu list
	private static void choosePoke(){
		int pok;								
		do{
			System.out.println("\nWhich Pokemon will be battling?\n");
			System.out.println(" #  Name        HP   Type");
			System.out.println("---------------------------------");
			for(int i=0;i<team.size();i++){
				team.get(i).menu(i+1);
			}
			System.out.println("\nEnter a number: ");
			pok = Integer.parseInt(kb.nextLine());
		}while(pok<0 || pok>team.size());
		
		current=team.get(pok-1);									// current stores said Pokemon
		
		System.out.printf("\n%s, I choose you!\n",current.getName());
	}	
	
	// randomly chooses a Pokemon for the computer to use by 'rolling a die', aka a random
	private static void compChoosePoke(){							
		Random dice=new Random();									
		int chosenOne=dice.nextInt(enemies.size());
		enemy=enemies.get(chosenOne);								// enemy stores said Pokemon
		String enemyName=enemy.getName();
			
		System.out.println("\nYour opponent will be... "+enemyName+"!");
	}    
}