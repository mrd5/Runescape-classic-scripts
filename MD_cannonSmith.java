import java.util.Arrays;
import java.util.Locale;
import java.text.DecimalFormat;
import javax.swing.JOptionPane;

//RUN IN FALADOR WEST BANK
//NOTHING IN INVENTORY (although a sleeping bag, cannon mold, and any number of steel bars would work)
//SAVE FILE AS MD_cannonSmith.java

public final class MD_cannonSmith extends Script {
	private static final int MOULD = 1057; //cannon ammo mould id
	private static final int STEEL_BAR = 171; //steel bar id
	private static final int SLEEPING_BAG = 1263; //sleeping bag id
	private static final int SMITHING = 13;//Smithing is the 13th skill
	private static final int CANNON_BALL = 1041; //cannonball id
	private static final int DOORS_CLOSED = 64; //closed door id

	private int start_xp; //starting smith xp before using script
	private int xp; //smith xp when gaining xp from smithing
	private int banked_count; //how many cannon balls banked

	private long start_time;//used to display how long script has been running
	private long star_time;//Used for smithing xp differences

	private final DecimalFormat iformat = new DecimalFormat("#,##0");

	public MD_cannonSmith(Extension ex){
		super(ex);
	}

	@Override
	public void init(String params){//Initialization, when script is first started
		start_time = -1L;
		star_time = -1L;
		banked_count = 0;
	}

	@Override
	public int main(){//This runs over and over for as long as the script is running

		if (start_time == -1L){//Gets current time
			start_time = System.currentTimeMillis();

		}

		if (star_time == -1L){//Gets current time and xp of smithing skill
			star_time = System.currentTimeMillis();
			start_xp = xp = getXpForLevel(13);
		} else {
			xp = getXpForLevel(13);
		}


		if (isQuestMenu()){//If talking to a banker, open the bank interface
			answer(0);
			return random(1000, 2000);
		}

		if (isBanking()){//While bank interface is open, deposit and withdraw necessary items
			int count = getInventoryCount(CANNON_BALL);
			if (count > 0){
				banked_count += count;
				deposit(CANNON_BALL, count);
			}

			if (getInventoryCount(MOULD) == 0){
				withdraw(MOULD, 1);
			}
			if (getInventoryCount(SLEEPING_BAG) == 0){
				withdraw(SLEEPING_BAG, 1);
			}
			if (getInventoryCount(STEEL_BAR) != 28){
				withdraw(STEEL_BAR, 28 - getInventoryCount(STEEL_BAR));
			}

			closeBank();
			return random(1000, 2000);
		}

		if (isAtApproxCoords(311, 545, 3) && getInventoryCount(STEEL_BAR) > 0){//If at furnace with materials to make cannon balls
			useItemOnObject(STEEL_BAR, 310, 546);//Use steel bar on furnace
			return random(1000, 2000);
		}

		if (!isBanking() && getInventoryCount(STEEL_BAR) == 28){//If in bank with materials to make cannon balls
			walkTo(311, 545);//Walk to furnace
			return random(1000, 2000);
		}


		if (getFatigue() > 98){//Sleep if fatigue is almost at 100
			useSleepingBag();
			return random(1000, 1500);
		}

		if (isAtApproxCoords(311, 545, 3) && getInventoryCount(STEEL_BAR) == 0){//If at furnace but out of materials
			walkTo(329, 552);//Walk back to bank
			return random(1000, 2000);
		}

		

		int[] banker = getNpcByIdNotTalk(BANKERS);//gets nearest banker which is free to talk to and talks to it
		if (banker[0] != -1){//if banker exists
			if (distanceTo(banker[1], banker[2]) > 2){
				if (checkDoors()){
					return random(1000, 2000);
				}
			}
			talkToNpc(banker[0]);
			return random(600, 800);
		}

		

		return 100;

	}

	@Override
	public void paint(){//Creates a display on screen to show run time, xp gained, and number of cannon balls banked
		final int orangey = 0xFFD900;
		final int white = 0xFFFFFF;

		int x = 25;
		int y = 25;

		drawString("Cannon Smither",x, y, 1, orangey);
		y += 15;

		drawString("Runtime: " + get_runtime(), x, y, 1, white);
		y += 15;

		int xp_gained = xp - start_xp;
		drawString("XP Gained: " + iformat.format(xp_gained) + " ("+ _perHour(xp_gained) + "/h)", x, y, 1, white);
		y += 15;

		drawString("Cannonballs banked: " + banked_count, x, y, 1, white);
		y += 15;
	}





	private String get_runtime() {//Gets run time of script in hours/mins/secs 
       long secs = ((System.currentTimeMillis() - start_time) / 1000L);
       if (secs >= 3600) {
           return (secs / 3600) + " hours, " +
                   ((secs % 3600) / 60) + " mins, " +
                   (secs % 60) + " secs.";
       }
       if (secs >= 60) {
           return secs / 60 + " mins, " +
                   (secs % 60) + " secs.";
       }
       return secs + " secs.";
   }

   private String _perHour(int total) {//Calculates how many cannon balls are being made per hour
      if (total <= 0 || star_time <= 0L) {
          return "0";
      }
      return iformat.format(
          ((total * 60L) * 60L) / ((System.currentTimeMillis() - star_time) / 1000L)
      );
  }

  	private boolean checkDoors(){//Checks if doors are closed
		int[] closed = getObjectById(DOORS_CLOSED);
		if (closed[0] == -1){
			return false;
		}
		atObject(closed[1], closed[2]);
		return true;
	}

}