package com.chess.engine.player;
/*
*    Notes:
*      + Enum:
*          + An enum is a special 'class' that represents a group of constants (unchangeable variables
*                   like final variables).
*          + You can access enum constants with dot notation
*          + you can have an enum inside a class
*          + you can loop through the values() method which returns an array of all enum constants
*
*          + Difference between Enums and Classes:
*              + An enum can, just like a class, have attributes and methods. The only difference is
*                  that enum constants are public, static and final(unchangeable - cannot be overridden)
*              + An enum cannot be used to create objects, and it cannot extend other classes
*                  (but it can implement interfaces).
*
*           + Why And When to Use Enums?:
*               + use enums when you have values that you know aren't going to change, like month, days,
*                colors, etc

* */
public enum MoveStatus
{
    DONE
        {
            @Override
            public boolean isDone()
            {
                return true;
            }
        },

    ILLEGAL_MOVE
        {
            @Override
            public boolean isDone()
            {
                return false;
            }
        },

    LEAVES_PLAYER_IN_CHECK
        {
            @Override
            public boolean isDone()
            {
                return false;
            }
        };

    public abstract boolean isDone();
}
