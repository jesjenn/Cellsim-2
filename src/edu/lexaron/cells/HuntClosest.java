/*
 *  Project name: CellSIM/HuntClosest.java
 *  Author & email: Mirza Suljić <mirza.suljic.ba@gmail.com>
 *  Date & time: Jun 13, 2016, 4:05:01 PM
 */
package edu.lexaron.cells;

import edu.lexaron.world.World;
import javafx.scene.image.Image;

/**
 * @author Mirza Suljić <mirza.suljic.ba@gmail.com>
 */
public class HuntClosest extends Cell {
  private static final Image GFX = new Image("edu/lexaron/gfx/huntClosest.png");

  public HuntClosest(String ID, int x, int y) {
    super(ID, x, y, 50, 3, 1, 1, 1);
  }

  public HuntClosest(World world) {
    this("C", getRandom().nextInt(world.getWidth()), getRandom().nextInt(world.getHeight()));
  }

  @Override
  public Image getImage() {
    return GFX;
  }

  @Override
  public Breed getBreed() {
    return Breed.HUNT_CLOSEST;
  }

  @Override
  public void doHunt(World w) {
    if (isAlive()) {
      if (getPath().isEmpty()) {
        if (w.getWorld()[getY()][getX()].getSugar().getAmount() <= 0) {
          setTargetFood(lookForFood(w));
          if (getTargetFood() != null) {
            findPathTo(getTargetFood());
            usePath(w);
          }
          else {
            for (int i = 1; i <= getSpeed(); i++) {
              moveLeft(w);
              randomStep(w);
            }
          }
        }
        else {
          eat(w);
        }
      }
      else {
        usePath(w);
      }
    }
  }

  /**
   * @param w
   * @return
   */
  @Override
  public int[] lookForFood(World w) {
    // Cell type CLOSEST is only interested in the CLOSEST sugar tile it finds.
//        System.out.println("Cell " + getGeneCode() + " looking for food from " + getX() + "," + getY() + "...");
    int[] foodLocation = new int[2];
    boolean found = false;
        outterloop:
    for (int v = 0; v <= getVision(); v++) {
      for (int i = (getY() - v); i <= (getY() + v); i++) {
        for (int j = (getX() - v); j <= (getX() + v); j++) {
          try {
//                    System.out.print("(" + j + "," + i + ")");        
            if (w.getWorld()[i][j].getSugar().getAmount() > 0) {
              foodLocation[0] = i; // Y
              foodLocation[1] = j; // X
//                                System.out.println("FOUND: " + j + "," + i);
              found = true;
              break outterloop;
            }
          }
          catch (ArrayIndexOutOfBoundsException ex) {

          }
        }
//            System.out.print("\n");
      }
    }
    if (found) {
//            System.out.println(getGeneCode() + " found food on " + foodLocation[0] + "," + foodLocation[1]);
      return foodLocation;
    }
    else {
//            System.out.println(getGeneCode() + " found no food.");
      return null;
    }

  }

  /**
   * @param w
   */
  @Override
  public void mutate(World w) {
    int[] childLocation = findFreeTile(w);
    HuntClosest child = new HuntClosest(String.valueOf(getGeneCode() + "." + getOffspring()), childLocation[1], childLocation[0]);
    child.inheritFrom(this);
    try {
      child.eat(w);
      child.evolve();
      w.getNewBornCells().add(child);
      setOffspring(getOffspring() + 1);
      setEnergy(getEnergy() / 3);
    }
    catch (Exception ex) {
      System.out.println(getGeneCode() + " failed to divide:\n" + ex);
    }
  }
}
