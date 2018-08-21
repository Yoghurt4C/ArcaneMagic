package com.raphydaphy.arcanemagic.tileentity;

import com.raphydaphy.arcanemagic.ArcaneMagic;
import com.raphydaphy.arcanemagic.api.AnimaReceiveMethod;
import com.raphydaphy.arcanemagic.api.IAnimaInductible;
import com.raphydaphy.arcanemagic.api.IAnimaReceiver;

public class TileEntityAltar extends TileEntityAnimaStorage implements IAnimaInductible, IAnimaReceiver
{
    private static final int CAPACITY = 1000;

    public TileEntityAltar()
    {
        super(ArcaneMagic.ALTAR_TE);
    }

    @Override
    public boolean takeAnima(int anima)
    {
        if (getCurrentAnima() >= anima)
        {
            this.anima -= anima;
            this.markDirty();
            return true;
        }
        return false;
    }

    @Override
    public boolean isFull()
    {
        return getCurrentAnima() >= CAPACITY;
    }

    @Override
    public boolean receiveAnima(int anima, AnimaReceiveMethod method)
    {
        if (method == AnimaReceiveMethod.SPECIAL)
        {
            if (!world.isRemote)
            {
                if (getCurrentAnima() + anima <= CAPACITY)
                {
                    this.anima += anima;
                } else
                {
                    this.anima = CAPACITY;
                }

                markDirty();
            }
            return true;
        }
        return false;
    }
}
