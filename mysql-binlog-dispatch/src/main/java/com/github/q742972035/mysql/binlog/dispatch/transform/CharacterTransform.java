package com.github.q742972035.mysql.binlog.dispatch.transform;

public class CharacterTransform implements ObjectTransform<Character> {
    @Override
    public Character transform(Object obj) {
        return obj.toString().toCharArray()[0];
    }
}
