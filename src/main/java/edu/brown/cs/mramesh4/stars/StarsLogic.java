package edu.brown.cs.mramesh4.stars;
import edu.brown.cs.mramesh4.CSVParser.CSVParser;
import edu.brown.cs.mramesh4.Dimensional.Dimensional;
import edu.brown.cs.mramesh4.KDTree.KDTree;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.Comparator;

/**
 * This is a class for the Stars List.
 */
public class StarsLogic implements ActionMethod<Star> {
  private ArrayList<Star> starList;
  private KDTree<Star> starTree;
  /**.
   * The constructor is a constructor of a Stars
   */
  public StarsLogic() {
    starList = new ArrayList<>();

  }

  /** .
   * Takes in a command from the command line and executes
   * it
   * @param coms String from the command line
   */
  @Override
  public HashMap<String, Object> run(String[] coms) {
    //split the command by whitespace
    //String[] coms = cmd.split(" ");
    //assuming the length > 0
    if (coms.length > 0) {
      //run regardless of uppercase and trimmed space
      switch (coms[0]) {
        //call stars
        case "stars":
          fillStarsListByComs(coms);
          break;
        //call naive_neighbors
        case "naive_neighbors":
          neighborsCall(coms);
          break;
        //call naive_radius
        case "naive_radius":
          radiusCall(coms);
          break;
        case "neighbors":
          neighborsCall(coms);
          break;
        case "radius":
          radiusCall(coms);
          break;
        default:
          System.err.println("ERROR: You entered" + coms[0] + "Please enter a valid command");
          break;
      }
    }
    return null;
  }

  /**.
   * Returns local arraylist of Stars
   * @return star arraylist
   */
  public ArrayList<Star> getStarList() {
    return starList;
  }

  /**.
   * This takes in the commands split by white space
   * and fills the star list. It checks for valid
   * files and header by calling a support method
   * @param coms
   *    String[] of whitespace seperated commands
   */
  public void fillStarsListByComs(String[] coms) {
    //if the coms is not the right length, throw an error
    if (coms.length != 2) {
      System.err.println("ERROR: THE STARS METHOD TAKES IN 2 COMMANDS");
    } else {
      CSVParser csv = new CSVParser();
      String filename = coms[1];
      //checks to see if the file is valid
      if (!csv.isValidFile(filename)) {
        System.err.println("ERROR: FILE NOT FOUND");
        return;
      }
      //parses the file and fills the star list
      ArrayList<List<String>> csvSave = csv.parseCSV(filename);
      if (csvSave == null) {
        return;
      }
      if (csv.getCols() != 5) {
        System.err.println("ERROR: WRONG COLUMNS");
        return;
      }
      List<String> heading = csvSave.get(0);
      if (!(heading.get(0).equals("StarID"))
          || !(heading.get(1).equals("ProperName"))
          || !(heading.get(2).equals("X"))
          || !(heading.get(3).equals("Y"))
          || !(heading.get(4).equals("Z"))) {
        System.err.println("ERROR: This file has a malformed header");
        return;
      }
      fillStarsList(csvSave);

      //if the star list is empty, throw an error
        //read the files
      System.out.println("Read " + starList.size()
          + " stars from " + filename);
    }
  }

  /**.
   * Fills the star list with input from a csv
   * @param csv
   *      List of List of row/cols from a csv
   */
  public void fillStarsList(ArrayList<List<String>> csv) {
    //clears the stars list
   // starList.clear();
    //check for a malformed header
    List<String> heading = csv.get(0);
    starList.clear();
    //remove the heading
    csv.remove(0);
    //for each row
    for (List<String> row: csv) {
      //try to create a star from the row
      try {
        starList.add(
            new Star(row.get(0), row.get(1), row.get(2), row.get(3),
            row.get(4)));
      } catch (IllegalArgumentException e) {
        //if it has malformed input, end clear the list and break
        System.err.println("ERROR: This file has malformed input");
        starList.clear();
        break;
      } catch (ArrayIndexOutOfBoundsException e) {
        System.err.println("ERROR: This file has malformed input");
        starList.clear();
        break;
      }
    }
    if (!starList.isEmpty()) {
      starTree = new KDTree<>(starList, 3);
    }
  }

  /**
   * This method returns a list of neighbors so our function can call it.
   * @param command A command to run
   * @param coms Command split by whitespace
   * @return a List of Stars
   */
  public List<Star> neighborsRet(String command, String[] coms) {
    boolean naive = true;
    if (coms[0].toUpperCase().equals("NEIGHBORS") || coms[0].equals("neighbors")) {
      naive = false;
    }
    //if the star-list is empty, print an error
    if (starList.isEmpty() || (!naive && starTree == null)) {
      System.err.println("ERROR: Please load a file to the Stars");
      return null;
    }
    //if the input contains a string, we want to remove
    //the " ", so we can split by whitespace
    if (!isClassInt(coms)) {
      //splits the " " by whitespace and returns an array
      coms = processComs(command, coms);
      //checks for invalid input
      if (coms == null) {
        System.err.println("ERROR: Please put valid input");
        return null;
      }
    }
    //if commands have wrong number of args, print an error
    List<Star> ret = new ArrayList<>();
    if (coms.length != 3 && coms.length != 5) {
      System.err.println("ERROR: THE NAIVE_NEIGHBORS "
          + "METHOD TAKES IN 3 or 5 COMMANDS");
    } else {
      //if the command is valid
      String k = coms[1];
      try {
        //get the k integer
        int knn = Integer.parseInt(k);
        //if it is negative or 0 throw an error
        if (knn < 0) {
          System.err.println("ERROR: Please "
              + "supply a non-negative integer");
          return null;
        }
        if (knn == 0) {
          return null;
        }
        //call different helpers based on args supplied
        if (coms.length == 3) {
          if (!naive) {
            ret = nearestNeighbors(knn, coms[2]);
          } else {
            ret = naiveNearestNeighbors(knn, coms[2]);
          }
          //print every star in the returned list
        } else {
          double x = Double.parseDouble(coms[2]);
          double y = Double.parseDouble(coms[3]);
          double z = Double.parseDouble(coms[4]);
          if (!naive) {
            ret = nearestNeighbors(knn, x, y, z);
          } else {
            ret = naiveNearestNeighbors(knn, x, y, z);
          }
        }
        if (ret == null) {
          System.err.println("ERROR: INVALID SEARCH");
          return null;
        } else if (!ret.isEmpty()) {
          return ret;
        }
      } catch (NumberFormatException e) {
        //if the number is not an integer, throw an exception
        System.err.println("ERROR: Please supply a valid integer");
        return null;
      }
    }
    return null;
  }
  /** .
   * Gets a naive_neighbors command and processes it
   * to go work with run
   * @param coms String array of coms
   */
  public void neighborsCall(String[] coms) {
    boolean naive = true;
    if (coms[0].toUpperCase().equals("NEIGHBORS") || coms[0].equals("neighbors")) {
      naive = false;
    }
    //if the star-list is empty, print an error
    if (starList.isEmpty() || (!naive && starTree == null)) {
      System.err.println("ERROR: Please load a file to the Stars");
      return;
    }
    if (coms == null) {
      System.err.println("ERROR: Please put valid input");
      return;
    }
    //if commands have wrong number of args, print an error
    List<Star> ret = new ArrayList<>();
    if (coms.length != 3 && coms.length != 5) {
      System.err.println("ERROR: THE NAIVE_NEIGHBORS "
          + "METHOD TAKES IN 3 or 5 COMMANDS");
    } else {
      //if the command is valid
      String k = coms[1];
      try {
        //get the k integer
        int knn = Integer.parseInt(k);
        //if it is negative or 0 throw an error
        if (knn < 0) {
          System.err.println("ERROR: Please "
              + "supply a non-negative integer");
          return;
        }
        if (knn == 0) {
          return;
        }
        //call different helpers based on args supplied
        if (coms.length == 3) {
          if (!naive) {
            ret = nearestNeighbors(knn, coms[2]);
          } else {
            ret = naiveNearestNeighbors(knn, coms[2]);
          }
          //print every star in the returned list
        } else {
          double x = Double.parseDouble(coms[2]);
          double y = Double.parseDouble(coms[3]);
          double z = Double.parseDouble(coms[4]);
          if (!naive) {
            ret = nearestNeighbors(knn, x, y, z);
          } else {
            ret = naiveNearestNeighbors(knn, x, y, z);
          }
        }
        if (ret == null) {
          System.err.println("ERROR: INVALID SEARCH");
          return;
        } else if (!ret.isEmpty()) {
          for (int j = 0; j < ret.size(); j++) {
            String str = ret.get(j).getStarID();
            System.out.println(str);
          }
        }
      } catch (NumberFormatException e) {
        //if the number is not an integer, throw an exception
        System.err.println("ERROR: Please supply a valid integer");
        return;
      }

    }
  }

  /**
   * Returns a list of Stars from radius search.
   * @param command command to run
   * @param coms command split by whitespace
   * @return a list of Stars
   */
  public List<Star> radiusRet(String command, String[] coms) {
    boolean naive = false;
    if (coms[0].toUpperCase().equals("RADIUS") || coms[0].equals("radius")) {
      naive = false;
    } else if (coms[0].toUpperCase().equals("NAIVE_RADIUS") || coms[0].equals("naive_radius")) {
      naive = true;
    }
    //if the star-list is empty, print an error
    if (starList.isEmpty() || !naive && starTree == null) {
      System.err.println("ERROR: Please load a file to the Stars");
      return null;
    }
    //if the class
    if (!isClassInt(coms)) {
      coms = processComs(command, coms);
      if (coms == null) {
        System.err.println("ERROR: Please enter valid input");
        return null;
      }
    }
    if (coms.length != 3 && coms.length != 5) {
      System.err.println("ERROR: THE Naive_radius METHOD TAKES IN 3 or 5 COMMANDS"
          + " AND YOU SUBMITTED" + coms.length);
    } else {
      //if the command is valid
      String radius = coms[1];
      try {
        //get the integer from radius
        int r = Integer.parseInt(radius);
        //if r is < 0, return an error
        if (r < 0) {
          System.err.println("ERROR: Please supply"
              + "a non-negative integer");
          return null;
        }
        List<Star> ret = new ArrayList<>();
        if (coms.length == 3) {
          //call nearest_radius with radius/name
          if (!naive) {
            ret = nearestRadius(r, coms[2]);
          } else {
            ret = naiveNearestRadius(r, coms[2]);
          }
        } else {
          double x = Double.parseDouble(coms[2]);
          double y = Double.parseDouble(coms[3]);
          double z = Double.parseDouble(coms[4]);
          if (!naive) {
            ret = nearestRadius(r, x, y, z);
          } else {
            ret = naiveNearestRadius(r, x, y, z);
          }
        }
        if (ret == null) {
          System.err.println("ERROR: Invalid Search");
          return null;
        } else if (!ret.isEmpty()) {
          return ret;
        }
      } catch (NumberFormatException e) {
        //if the integer supplied in parseint isn't an int
        System.err.println("ERROR: Please supply a valid integer");
        return null;
      }
    }
    return null;
  }
  /**.
   * Takes the naive_radius command and processes it.
   * @param coms A string[] of commands split by whitespace
   */
  public void radiusCall(String[] coms) {
    boolean naive = false;
    if (coms == null) {
      System.err.println("ERROR: Please enter valid input");
      return;
    }
    if (coms[0].toUpperCase().equals("RADIUS") || coms[0].equals("radius")) {
      naive = false;
    } else if (coms[0].toUpperCase().equals("NAIVE_RADIUS") || coms[0].equals("naive_radius")) {
      naive = true;
    }
    //if the star-list is empty, print an error
    if (starList.isEmpty() || !naive && starTree == null) {
      System.err.println("ERROR: Please load a file to the Stars");
      return;
    }
    if (coms.length != 3 && coms.length != 5) {
      System.err.println("ERROR: THE Naive_radius METHOD TAKES IN 3 or 5 COMMANDS"
          + " AND YOU SUBMITTED" + coms.length);
    } else {
      //if the command is valid
      String radius = coms[1];
      try {
        //get the integer from radius
        int r = Integer.parseInt(radius);
        //if r is < 0, return an error
        if (r < 0) {
          System.err.println("ERROR: Please supply"
              + "a non-negative integer");
          return;
        }
        List<Star> ret = new ArrayList<>();
        if (coms.length == 3) {
          //call nearest_radius with radius/name
          if (!naive) {
            ret = nearestRadius(r, coms[2]);
          } else {
            ret = naiveNearestRadius(r, coms[2]);
          }
        } else {
          double x = Double.parseDouble(coms[2]);
          double y = Double.parseDouble(coms[3]);
          double z = Double.parseDouble(coms[4]);
          if (!naive) {
            ret = nearestRadius(r, x, y, z);
          } else {
            ret = naiveNearestRadius(r, x, y, z);
          }
        }
        if (ret == null) {
          System.err.println("ERROR: Invalid Search");
          return;
        } else if (!ret.isEmpty()) {
          for (int j = 0; j < ret.size(); j++) {
            String str = ret.get(j).getStarID();
            System.out.println(str);
          }
        }
      } catch (NumberFormatException e) {
        //if the integer supplied in parseint isn't an int
        System.err.println("ERROR: Please supply a valid integer");
        return;
      }
    }
  }

  /**.
   * Calculates the k nearest neighbors to a point in space
   * @param k a non-negative int representing number of neighbors
   * @param x  a double representing our point's x value
   * @param y a double representing our point's y value
   * @param z  a double representing our point's z value
   * @return a List of Stars closest to the point
   */
  public List<Star> naiveNearestNeighbors(int k, double x, double y, double z) {
    ArrayList<Star> ret = new ArrayList<>();
    if (k == 0) {
      return ret;
    }
    //get the star we want to use
    Star toFind = findStar(x, y, z);

    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      System.err.println("ERROR: There is no star with these coordinates ");
      return null;
    }

    //create a hashmap that stores a list of doubles and stars with the distance
    HashMap<Double, ArrayList<Star>> map = new HashMap<>();

    //add each star into a hashmap based on its distance ;
    for (Star star: starList) {
      double distance = star.calculateDistanceFromStar(toFind);
      if (!map.containsKey(distance)) {
        map.put(distance, new ArrayList<Star>());
      }
      map.get(distance).add(star);
    }

    //sort the entries into a list of keys;
    ArrayList<Double> keys = new ArrayList<>(map.keySet());
    //sort the keys in order from descending to ascending
    Collections.sort(keys);
    //a counter to go through the list
    int start = 0;
    //while we still have keys to go through and more neighbors:
    while (start < keys.size() && k > 0) {
      //get the nextClosestStars
      ArrayList<Star> nextClosestStars = map.get(keys.get(start));
      //subtract k
      int size = nextClosestStars.size();
      k -= size;
      //if k is below 0, that means there was
      // overflow by the size - k, so we need to select
      //size - k elements to add
      if (k < 0) {
        for (int i = 0; i < size - Math.abs(k); i++) {
          //generate a randomInt based on the current remaining stars
          int randomInt = (int) (Math.random()
              * (nextClosestStars.size()));
          //add the star
          ret.add(nextClosestStars.get(randomInt));
          //remove it from the list of remaining stars
          nextClosestStars.remove(randomInt);
        }
        //since we have hit k we are done;
        break;
      } else {
        //add the nextStar
        for (Star nextStar: nextClosestStars) {
          ret.add(nextStar);
        }
        //increment start;
        start++;
      }
    }
    //return our arrayList
    return ret;
  }
  /**.
   * Calculates the k nearest neighbors to a star with name
   * @param k Non-negative number of neighbors to find
   * @param name Name of the star
   * @return a List of Stars closest to the Star searched
   * for
   */
  public List<Star> naiveNearestNeighbors(int k, String name) {
    ArrayList<Star> ret = new ArrayList<>();
    if (k == 0) {
      return ret;
    }
    //get the star we want to use
    Star toFind = findStar(name);
    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      System.err.println("ERROR: There is no star with this name");
      return null;
    }
    //create a hashmap that stores a list of doubles and stars with the distance
    HashMap<Double, ArrayList<Star>> map = new HashMap<>();

    //add each star into a hashmap based on its distance ;
    for (Star star: starList) {
      if (!star.getStarID().equals(toFind.getStarID())) {
        double distance = star.calculateDistanceFromStar(toFind);
        if (!map.containsKey(distance)) {
          map.put(distance, new ArrayList<Star>());
        }
        map.get(distance).add(star);
      }
    }

    //System.out.println("Map full" + map.size());

    //sort the entries into a list of keys;
    ArrayList<Double> keys = new ArrayList<>(map.keySet());
    //sort the keys in order from descending to ascending
    Collections.sort(keys);
    //a counter to go through the list
    int start = 0;
    //while we still have keys to go through and more neighbors:
    while (start < keys.size() && k > 0) {
      //get the nextClosestStars
      ArrayList<Star> nextClosestStars = map.get(keys.get(start));
      //subtract k
      int size = nextClosestStars.size();
      k -= size;
      //if k is below 0, that means there was
      // overflow by the size - k, so we need to select
      //size - k elements to add
      if (k < 0) {
        for (int i = 0; i < size - Math.abs(k); i++) {
          //generate a randomInt based on the current remaining stars
          int randomInt = (int) (Math.random()
              * (nextClosestStars.size()));
          //add the star
          ret.add(nextClosestStars.get(randomInt));
          //remove it from the list of remaining stars
          nextClosestStars.remove(randomInt);
        }
        //since we have hit k we are done;
        break;
      } else {
        //add the nextStar
        for (Star nextStar: nextClosestStars) {
          ret.add(nextStar);
        }
        //increment start;
        start++;
      }
    }
    //return our arrayList
    return ret;
  }

  /**.
   * Takes a point and finds all stars within a radius
   * @param r Positive radius to search for from point
   * @param x a double representing our point's x value
   * @param y a double representing our point's y value
   * @param z a double representing our point's z value
   * @return a List of stars within the radius
   */
  public List<Star> naiveNearestRadius(int r, double x, double y, double z) {
    //if the list is empty, return null
    ArrayList<Star> ret = new ArrayList<Star>(starList);
    //get the star we want to use
    Star toFind = findStar(x, y, z);

    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    //for all stars whose radius is >, we remove them from the list
    ArrayList<Star> toRemove = new ArrayList<>();
    for (Star s: ret) {
      if ((double) s.calculateDistanceFromStar(toFind) > (double) r) {
        toRemove.add(s);
      }
    }
    for (Star s: toRemove) {
      ret.remove(s);
    }
    //we sort the list by the minimum distance
    Collections.sort(ret, new Comparator<>() {
      @Override
      public int compare(Star o1, Star o2) {
        return Double.compare(o1.calculateDistanceFromStar(toFind),
          o2.calculateDistanceFromStar(toFind));
      }
    });
    //return the lsit
    return ret;
  }
  /**.
   * Takes a point and finds all stars within a radius
   * from our star
   * @param r Positive radius to search for from point
   * @param name A name of a star to search from
   * @return a List of stars within the radius
   */
  public List<Star> naiveNearestRadius(int r, String name) {
    ArrayList<Star> ret = new ArrayList<Star>(starList);
    //get the star we want to use
    Star toFind = findStar(name);

    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    List<Star> toRemove = new ArrayList();
    toRemove.add(toFind);
    //for all stars whose radius is >, we remove them from the list
    for (Star s: ret) {
      if (s.calculateDistanceFromStar(toFind) > (double) r) {
        toRemove.add(s);
      }
    }
    ret.removeAll(toRemove);
    //ret.remove(toFind);
    //we sort the list by the minimum distance
    Collections.sort(ret, new Comparator<>() {
      @Override
      public int compare(Star o1, Star o2) {
        return Double.compare(o1.calculateDistanceFromStar(toFind),
          o2.calculateDistanceFromStar(toFind));
      }
    });

    return ret;
  }
  /**.
   * Takes a point and checks if a star exists at that
   * point
   * @param x a double representing our point's x value
   * @param y a double representing our point's y value
   * @param z a double representing our point's z value
   * @return a star with the x,y,z value
   */
  public Star findStar(double x, double y, double z) {
    for (Star star: starList) {
      if (star.getX() == x && star.getY() == y && star.getZ() == z) {
        return star;
      }
    }
    return new Star(" ", " ", Double.toString(x), Double.toString(y), Double.toString(z));
  }
  /**.
   * Takes a name and checks if a star exists with that name
   * @param name A name of a star
   * @return a star with the x,y,z value
   */
  public Star findStar(String name) {
    for (Star star: starList) {
      if (star.getName().equals(name)) {
        return star;
      }
    }
    return null;
  }
  /**.
   * Takes in the command line for naive_neighbors
   * and radius and sees if the 3rd arg is an int
   * @param scale
   *             string[] of commands
   * @return boolean representing if it takes an int
   */
  public boolean isClassInt(String[] scale) {
    try {
      Double.parseDouble(scale[2]);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  /**.
   * Takes in the command line for naive_neighbors
   * and radius and sees if the 3rd arg is an int
   * @param coms
   *             string representing the command
   * @param cmd
   *           String[] of commands
   * @return   String[] that removes the " " from a string
   */
  public String[] processComs(String coms, String[] cmd) {
    //gets the substring from the first " to the last word which should be "
    String s = coms.substring(coms.indexOf('"') + 1, coms.length() - 1);
    //checks for invalid quotes [if the last item is not a quote]
    if (coms.charAt(coms.length() - 1) != '"') {
      return null;
    }
    //makes sure no double quotes exist
    if (s.indexOf('"') == -1) {
      return new String[]{cmd[0], cmd[1], s};
    }
    return null;
  }


  /** .
   * Takes in a name and an integer to calculate nearest neighbors
   * @param knn number of neighbors to get
   * @param name star to serarch from
   * @return list of stars to print
   * @throws IllegalArgumentException if illegal arguments are passed in.
   */
  public List<Star> nearestNeighbors(int knn, String name) throws IllegalArgumentException {
    //get the star we want to use
    Star toFind = findStar(name);
    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    //get list of nearestNeighbors

    List<Star> s = starTree.nearestNeighbors(knn, toFind, true);
    //System.out.println("size" + s.size());
    ArrayList<Star> type = new ArrayList<>();
    for (Star a: s) {
      type.add(a);
    }
    type.remove(toFind);
    //System.out.println("type size" + type.size());
    return type;
  }

  /**.
   * Takes in a (x,y,z) and an integer to calculate nearest neighbors
   * @param knn number of neigbhors
   * @param x double x representing x coordinate
   * @param y double y representing y coordinate
   * @param z double z representing z coordinate
   * @return a list of star representing neighbors
   */
  public List<Star> nearestNeighbors(int knn, double x, double y, double z) {
    //ArrayList<Star> ret = new ArrayList<Star>(starList);
    //get the star we want to use
    Star toFind = findStar(x, y, z);
    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    List<Star> s = starTree.nearestNeighbors(knn, toFind, false);
    //System.out.println("size" + s.size());
    ArrayList<Star> type = new ArrayList<>();
    for (Star a: s) {
      type.add(a);
    }
    //System.out.println("type" + type.size());
    return type;
  }
  /**
   *.
   * @param r  Radius to search from
   * @param name Name of star to search from
   * @return a list of stars
   */
  public List<Star> nearestRadius(int r, String name) {
    Star toFind = findStar(name);
    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    //call the star list to get the nearest neighbors
    List<Dimensional> s = starTree.nearestRadius(r, toFind, true);
    //System.out.println("s" + s.size());
    ArrayList<Star> type = new ArrayList<>();
    for (int i = 0; i < s.size(); i++) {
      Star get = (Star) s.get(i);
      //System.out.println("ID" + get.getStarID());
      type.add(get);
    }
    //System.out.println("size" + s.size());
    //cast them to star list
    return type;
  }

  /**.
   * Call recurseradius using the Star point w x,y,z params
   * @param r an integer radius
   * @param x a double representing an x coordinate
   * @param y a double representing a y coordinate
   * @param z a double representing a z coordinate
   * @return a list of Star
   */
  public List<Star> nearestRadius(int r, double x, double y, double z) {
    Star toFind = findStar(x, y, z);
    //if the star doesn't exist, no neighbors exist
    if (toFind == null) {
      return null;
    }
    //get the nearest radius list
    List<Dimensional> s = starTree.nearestRadius(r, toFind, false);
    ArrayList<Star> type = new ArrayList<>();
    //System.out.println("size" + s.size());
    //cast it to a star
    for (int i = 0; i < s.size(); i++) {
      Star get = (Star) s.get(i);
      //System.out.println("ID" + get.getStarID());
      type.add(get);
    }
    return type;
  }
  /**.
   * Accessor method for KDTree
   * @return the KDTree
   */
  public KDTree<Star> getKDTree() {
    return starTree;
  }
}



