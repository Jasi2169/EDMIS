// Decompiled by DJ v3.7.7.81 Copyright 2004 Atanas Neshkov  Date: 2/12/2004 17:14:35
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   Deck.java

package com.progdan.zipengine.apps;

import java.util.Random;

public class Deck
{

    public int ncards()
    {
        return ncards;
    }

    public Deck(int i)
    {
        r = new Random();
        cards = new int[i];
        ncards = i;
        for(int j = 0; j < i; j++)
            cards[j] = j;

    }

    public void discard(int i)
    {
        for(int j = 0; j < i; j++)
            draw();

    }

    public int draw()
    {
        int i = r.nextInt();
        if(i < 0)
            i = -i;
        i %= ncards;
        ncards--;
        int j = cards[ncards];
        cards[ncards] = cards[i];
        cards[i] = j;
        return cards[ncards];
    }

    public Random r;
    private int cards[];
    private int ncards;
}
