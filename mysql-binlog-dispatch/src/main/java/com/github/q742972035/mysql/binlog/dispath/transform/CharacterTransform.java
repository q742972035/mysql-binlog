package com.github.q742972035.mysql.binlog.dispath.transform;

public class CharacterTransform implements ObjectTransform<Character> {
    @Override
    public Character transform(Object obj) {
        return obj.toString().toCharArray()[0];
    }
}
