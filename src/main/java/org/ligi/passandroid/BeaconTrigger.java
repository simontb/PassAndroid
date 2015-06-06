package org.ligi.passandroid;

import org.altbeacon.beacon.Beacon;

public class BeaconTrigger {

    private final String id1;
    private final String id2;
    private final String id3;

    public BeaconTrigger(final String id1, final String id2, final String id3) {
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
    }

    public String getId1() {
        return id1;
    }

    public String getId2() {
        return id2;
    }

    public String getId3() {
        return id3;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BeaconTrigger)) {
            return false;
        }

        BeaconTrigger other=(BeaconTrigger)o;

        return (getId1().toString().equals(other.getId1())) &&
               (getId2().toString().equals(other.getId2())) &&
               (getId2().toString().equals(other.getId2()));
    }
}
