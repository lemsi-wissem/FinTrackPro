package com.fintrack.domain.port.out;

public interface TokenBlacklistPort {
    void blacklist(String tokenId, long ttlSeconds);
    boolean isBlacklisted(String tokenId);
}
