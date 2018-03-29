/*
 * Helen Zhang
 * Pokemon.java
 *
 * The class in which anything related to the Pokemon themselves is done (the Attack class is below this one).
 * Storing the stats, manipulating those stats, taking damage, etc.
 * 
 */

import java.util.*;
class Pokemon {
	
	private int hp,energy,atkn,hpmax;			// declare necessary variables
	private String name,type,res,wkn;
	private boolean stun,disable;
	
	private Attack[]attacks;					// attacks will be added to by the Attack class
	
	Scanner kb=new Scanner(System.in);
	Random dice=new Random();
	
	// split the lines of input into a list and store them in variables to manipulate later
	// store the attacks with the Attack class and enter them into a list for accesss
	public Pokemon(String line){
		String[]stats=line.split(",");			
		name=stats[0];
		hp=Integer.parseInt(stats[1]);
		hpmax=Integer.parseInt(stats[1]);	// not technically part of the file, but it needs to be set
		type=stats[2];
		res=stats[3];
		wkn=stats[4];
		atkn=Integer.parseInt(stats[5]);
		attacks=new Attack[atkn];
		for(int i=0;i<atkn;i++){
			Attack a=new Attack(line,i);
			attacks[i]=a;
		}
		energy=50;							// same thing over here
	}

	// used in displaying the Pokemon when choosing.
	public void menu(int i){					
		System.out.printf("%2d. %-12s%-4d %-8s\n",i,name,hp,type);
	}	
		
	// used in displaying the Pokemon's stats between turns
	public void statMenu(){						
		System.out.printf("%-13s%-4d %-4s\n",name,hp,energy);
	}	
		
	// several annoying .get functions/methods
	public String getName(){					
		return name;
	}
	public int getHp(){
		return hp;
	}
	public int getEnergy(){
		return energy;
	}
	public int getAtkn(){
		return atkn;
	}
	public boolean getStun(){
		return stun;
	}
	
	// called by roundOver() in the Pokemon Arena class, this part just adds energy and makes sure it doesn't go over the max
	// also resets the stun 
	public void roundOverEnergyStun(){				
		energy+=10;
		if(energy>50){							 
			energy=50;
		}
		if(stun==true){
			stun=false;			
		}
	}
	
	// similar to roundOverEnergy, this is called by battleOver() in Pokemon Arena
	// it also functions the same way (minus the stun)
	public void battleOverHpEnergy(){					
		hp+=20;
		if(hp>hpmax){							
			hp=hpmax;
		}
		energy=50;
	}
	
	/* chooses a random attack for the computer
	 * some number manipulation to prevent trying to choose an attack from positions like [-1]
	 * calls the actual attack()
	 * only allowed to attack if the Pokemon is not stunned, print feedback if it is
	 */
	public void randomAttack(Pokemon current){
		if(stun==false){
			if(atkn!=1){							 
				int choose=dice.nextInt(atkn-1);
				attack(current,choose+1);
			}
			else{
				attack(current,atkn);				 
			}
		}
		else{
			System.out.println(getName()+" has been stunned! It cannnot attack!");
		}
		
	}
	
	//	allows the player to choose an attack from a given menu (if it isn't stunned) , then calls attack()
	// also prints some feedback if the pokemon is stunned and cannot attack
	public void chooseAttack(Pokemon enemy){	
		if(stun==false){
			System.out.println("\nChoose your attack!\n");
			System.out.println("#  Name               Energy Cost  Damage  Special");
			System.out.println("----------------------------------------------------");
			int atkc;
			do{			
				for(int i=0;i<atkn;i++){
					System.out.println((i+1)+". "+attacks[i]);
				}
				System.out.println("\nEnter a number: ");
				atkc=Integer.parseInt(kb.nextLine()); 
			}while(atkc<0 || atkc>atkn);
			
			attack(enemy,atkc);			
		}
		else{
			System.out.println(getName()+" has been stunned! It cannnot attack!");
		}
						
	}
	
	
	/* the attack function/method used for both computer and player
	 * check to make sure the attack can be performed, call damage, which calculates, well, damage
	 * subtract said damage, keep the hp 0 and above, and do the same with energy
	 * if an attack cannot be afforded, print a message for forced passing when the Pokemon doesn't have enough energy to perform an attack
	 */
	private void attack(Pokemon victim, int chosenAttackNumber){			// a new parameter name (victim) is used for generalization	
		Attack chosen=attacks[chosenAttackNumber-1];				
		int ecost=chosen.getEnergyCost();
		if(energy>=ecost){											
			System.out.println("\n"+getName()+" is attacking "+victim.getName()+"!");
			System.out.println(getName()+" used "+chosen.getName()+"!");
			
			int atkdmg=chosen.getDamage();							
			String spc=chosen.getSpecial();
			
			int dmg=damage(victim,atkdmg,spc);
			if(dmg<0){
				dmg=0;
			}					
			System.out.println("It dealt "+dmg+" damage!");
			
			victim.hp-=dmg;										 
			
			if(victim.hp<=0){									
				victim.hp=0;
			}
			
			energy-=ecost;
			
			if(energy<0){										
				energy=0;
			}
			if(energy>50){
				energy=50;
			}
		}
		else{													
			System.out.println("\nNot enough energy! "+getName()+" has passed this turn.");
		}
	}	
	
	/* damage() handles special attacks as well as normal damage
	 * if there is no special, simply add the damage to the total
	 * for the various special attacks, a 'coin flip' is used to determine chances of wild card, wild storm, and stun
	 * of course, if it fails, inform the user
	 * after the damage is calculated, call damageMult() for type mulitpliers
	 * after all total damage is calculated, -10 for disable, and return the damage to attack()
	 */
	private int damage(Pokemon victim, int atkdmg, String atkspc){				
		int totdmg=0;
		if(atkspc.equals(" ")){												
			totdmg+=atkdmg;
		}
		else{
			if(atkspc.equals("wild card")){										
				int flip=dice.nextInt(2);										
				if(flip==1){
					System.out.println(getName()+" used a wild card attack!");
					totdmg+=atkdmg;
				}
				else{																// print feedback for successful and failed attacks
					System.out.println(getName()+"'s wild card attack failed!");
				}
			}			
			else if(atkspc.equals("wild storm")){
				int flip2=1;
				if(flip2==0){
					System.out.println(getName()+"'s wild storm attack failed!");
				}
				while(flip2==1){
					flip2=dice.nextInt(2);
					System.out.println(getName()+" used a wild storm attack!");
					totdmg+=atkdmg;
				}
				
			}			
			else if(atkspc.equals("stun")){
				int flip3=dice.nextInt(2);
				if(flip3==1){
					victim.stun=true;
					System.out.println(victim.getName()+" has been stunned!");
				}
				else{
					System.out.println(getName()+"'s stun attack failed!");
				}
				totdmg+=atkdmg;
			}			
			else if(atkspc.equals("disable")){
				if(!victim.disable==true){											// ... as well as other special effects
					victim.disable=true;
					System.out.println(victim.getName()+" has been disabled!");
				}
				else{
					System.out.println(victim.getName()+" is already disabled!");
				}				
				totdmg+=atkdmg;
			}			
			else if(atkspc.equals("recharge")){
				energy+=20;
				System.out.println(getName()+" has recharged its energy!");
				totdmg+=atkdmg;
			}
		}		
				
		totdmg=damageMult(victim,totdmg);				
		if(disable==true){									
			totdmg-=10;
		}											
		return totdmg;										
	}
	
	
	// Multiply the total damage according to the type, and print some feedback
	// return the damage to damage()
	private int damageMult(Pokemon victim, int dmg){
		if(type.equals(victim.wkn)){
			dmg*=2;
			System.out.println("The attack was super effective!");
		}
		else if(type.equals(victim.res)){									
			dmg*=0.5;														
			System.out.println("The attack was not very effective...");
		}
		else{
			System.out.println("The attack was unaffected by type.");
		}
		return dmg;
	}	
}


/*
 * Helen Zhang
 * Pokemon.java
 *
 * The class in which things associated solely with attacks are handled. Which is basically getting and
 * storing the stats. Such as name, damage, special effect, etc.
 *
 */
class Attack{
	
	private String name,special;				// necessary variables
	private int ecost,dmg;
	
	// store information into variables with the same method as the Pokemon class
	// minimal math to get to the right spot
    public Attack(String line,int i){
    	String[]stats=line.split(",");
    	name=stats[6+i*4];
    	ecost=Integer.parseInt(stats[7+i*4]); 
    	dmg=Integer.parseInt(stats[8+i*4]);
    	special=stats[9+i*4];
    }
    
    // to String method used in the choosing attack menu
    public String toString(){					
		return String.format("%-20s     %-2d        %-2d    %-10s",name,ecost,dmg,special);
	}
	
	// several MORE annoying .get functions/methods
    public int getEnergyCost(){					
    	return ecost;
    }    
    public int getDamage(){
    	return dmg;
    }    
    public String getSpecial(){
    	return special;
    }
    public String getName(){
    	return name;
    }
		
    
}