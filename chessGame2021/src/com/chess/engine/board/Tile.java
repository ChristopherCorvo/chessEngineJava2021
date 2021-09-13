package com.chess.engine.board;

/*
* From: https://www.geeksforgeeks.org/packages-in-java/
*
* Package in java is a mechanism to encapsulate a group of classes, sub classes,
* sub packages and interfaces.
*
* Packages are used for:
*   + Preventing naming conflicts. For example there can be two classes with
*     the name Employee in two packages.
*   + Making searching/locating and usage of classes, interfaces, enumerations and
*     annotations easier.
*   + Providing controlled access: protected and default have package level access control.
*     A protected member is accessible by classes in the same package and its subclasses.
*     A default member (without any access specifier) is accessible by classes in
*     the same package only.
*   + Packages can be considered as data encapsulation ( or data-hiding)
*
* All we need to do is put related classes into packages. After that, we can
* simply write an import class from existing packages and use it in our program.
* A package is a container of a group of related classes where some of the classes
* are accessible are exposed and others are kept for internal purposes
*
* We can reuse existing classes from the packages as many times as we need it
* in our program.
* */
/*
* Abstract class = template
* From: https://www.techopedia.com/definition/24335/abstract-class-java
* An abstract class in java means that the class is a superclass
* and that it cannot be instantiated and is used to state or define general
* characteristics. An object cannot be formed from a Java abstract class;
* trying to instantiate an abstract class only produces a compiler error.
*
* Subclasses extended from an abstract class have all the abstract class's
* attributes, in addition to attributes specific to each subclass. The abstract
* class states the class characteristics and methods for implementation,
* thus defining a whole interface.
*
* Difference between an abstract class and an interface. An interface only has
* method declarations or abstract methods and constant data members, while
* an abstract class may have abstract methods, member variables and concrete
* methods. Because Java only supports single inheritance, a class can implement
* several interfaces but can extend only one abstract class.
* */

import com.chess.engine.pieces.Piece;
import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile
{
    // members
    /*
    * By making this variable protected it can only be accessed by its subclasses
    *
    * By making it final it means it can only be set once i.e a constant variable
    *
    * */
    protected final int tileCoordinate;

    private static final Map<Integer, EmptyTile> EMPTY_TILES_CACHE = createAllPossibleEmptyTiles();

    /*
    * A java Map is an object that maps keys to values. A map cannot contain duplicate keys. Each
    * key can map to at most one value.
    *
    * When you instantiate a new java map you need to specify the data type of the key and value
    * */
    private static Map<Integer, EmptyTile> createAllPossibleEmptyTiles()
    {
        final Map<Integer, EmptyTile> emptyTileMap = new HashMap<>();

        for(int i=0; i<BoardUtils.NUM_TILES; i++)
        {
            emptyTileMap.put(i, new EmptyTile(i));
        }
        /*
        * A map is a container, the ImmutableMap method says that once the Map is made
        * no one can change it.
        *
        * ImmutableMap comes from the google open source library called guava
        * From: https://github.com/google/guava/releases/tag/v30.1.1
        * */
        return ImmutableMap.copyOf(emptyTileMap);
    }



    /*
    *
    * Note: The constructor of this abstract class is marked as private. Why? Well we dont want someone
    * to be able to create more tiles from outside the class. Remember we are making a chess board that
    * will only have 64 eventual tiles.
    *
    * The only way a person can create a new tile is via a new factory method called createTile. If
    * they want an empty tile they will only get one of the cache emptyTiles otherwise they will get
    * one of the empty tiles.
    * */

    public static Tile createTile(final int tileCoordinate, final Piece piece)
    {
        return piece != null ? new OccupiedTile(tileCoordinate, piece) : EMPTY_TILES_CACHE.get(tileCoordinate);
    }

    // constructor
    private Tile(final int tileCoordinate)
    {
        this.tileCoordinate = tileCoordinate;
    }

    // methods
    /*
    * An abstract method is a method that is declared without an implementation.
    *
    * When an abstract class is subclassed, the subclass usually provides
    * implementations for all of the abstract methods in its parent class.
    * However, if it does not, then the subclass must also be declared abstract
    * */
    public abstract boolean isTileOccupied();

    public abstract Piece getPiece();

    public int getTileCoordinate()
    {
        return this.tileCoordinate;
    }

    // ----------------------------------------
    // subclass
    /*
    * In Java, items with the final modifier cannot be changed!
    *
    * A final class can't be extended ---> this means it can not be inherited
    * A final variable cannot be reassigned another value
    * A final method cannot be overridden
    *
    * ? What is a static class and why use it?
    * */

    /*
    * Regarding the nested classes ---> these subclasses could have been into their own files.
    * If we had made the subclasses their own files then we would not have had to use the static keyword.
    * we use the static keyword so that they dont inherit the state of tile.
    *
    * Reminder: possible refactor this code with the subclasses made into their own files.
    *
    * */
    public static final class EmptyTile extends Tile {

        // constructor
        private EmptyTile(final int coordinate)
        {
            // calls the parent class Tile constructor
            super(coordinate);
        }

        // since this child class inherits from the parent Tile class we need
        // override the parent methods

        @Override
        public String toString()
        {
            return "-";
        }

        @Override
        public boolean isTileOccupied()
        {
            return false;
        }

        @Override
        public Piece getPiece()
        {
            return null;
        }
    }

    // ----------------------------------------
    // subclass
    public static final class OccupiedTile extends Tile
    {
        // member
        /*
        * there is no way to reference this variable from outside the class
        *
        * And the variable is immutable i.e constant
        * */

        private final Piece pieceOnTile;

        // constructor
        private OccupiedTile(int tileCoordinate, final Piece pieceOnTile)
        {
            super(tileCoordinate);
            this.pieceOnTile = pieceOnTile;
        }

        // methods

        @Override
        public String toString()
        {
            return getPiece().getPieceAllegiance().isBlack() ? getPiece().toString().toLowerCase() :
                   getPiece().toString();
        }

        @Override
        public boolean isTileOccupied()
        {
            return true;
        }

        @Override
        public Piece getPiece()
        {
            return this.pieceOnTile;
        }
    }
}
