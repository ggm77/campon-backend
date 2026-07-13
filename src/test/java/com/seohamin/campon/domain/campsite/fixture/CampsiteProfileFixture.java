package com.seohamin.campon.domain.campsite.fixture;

import com.seohamin.campon.domain.campsite.model.CampsiteProfile;
import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * 테스트용 CampsiteProfile 생성 빌더 (record라 생성자 인자가 많아 기본값 + 필요한 것만 덮어쓰기)
 */
public final class CampsiteProfileFixture {

    private Set<Facility> facilities = Set.of();
    private Set<Equipment> rentalEquipments = Set.of();
    private Integer distance = 1000;
    private int autoSiteCount = 0;
    private int caravanSiteCount = 0;
    private boolean trailerAllowed = false;
    private boolean caravanAllowed = false;
    private boolean insured = false;
    private int fireSafetyCount = 0;
    private boolean operating = true;
    private LocalDate closedFrom = null;
    private LocalDate closedTo = null;
    private List<String> operationSeasons = List.of();

    public static CampsiteProfileFixture aProfile() {
        return new CampsiteProfileFixture();
    }

    public CampsiteProfileFixture facilities(final Set<Facility> facilities) {
        this.facilities = facilities;
        return this;
    }

    public CampsiteProfileFixture rentalEquipments(final Set<Equipment> rentalEquipments) {
        this.rentalEquipments = rentalEquipments;
        return this;
    }

    public CampsiteProfileFixture distance(final Integer distance) {
        this.distance = distance;
        return this;
    }

    public CampsiteProfileFixture autoSiteCount(final int autoSiteCount) {
        this.autoSiteCount = autoSiteCount;
        return this;
    }

    public CampsiteProfileFixture caravanSiteCount(final int caravanSiteCount) {
        this.caravanSiteCount = caravanSiteCount;
        return this;
    }

    public CampsiteProfileFixture trailerAllowed(final boolean trailerAllowed) {
        this.trailerAllowed = trailerAllowed;
        return this;
    }

    public CampsiteProfileFixture caravanAllowed(final boolean caravanAllowed) {
        this.caravanAllowed = caravanAllowed;
        return this;
    }

    public CampsiteProfileFixture insured(final boolean insured) {
        this.insured = insured;
        return this;
    }

    public CampsiteProfileFixture fireSafetyCount(final int fireSafetyCount) {
        this.fireSafetyCount = fireSafetyCount;
        return this;
    }

    public CampsiteProfileFixture operating(final boolean operating) {
        this.operating = operating;
        return this;
    }

    public CampsiteProfileFixture closedBetween(final LocalDate from, final LocalDate to) {
        this.closedFrom = from;
        this.closedTo = to;
        return this;
    }

    public CampsiteProfileFixture operationSeasons(final List<String> operationSeasons) {
        this.operationSeasons = operationSeasons;
        return this;
    }

    public CampsiteProfile build() {
        return new CampsiteProfile(
                1L,
                "테스트 캠핑장",
                null,
                null,
                37.5,
                127.0,
                distance,
                null,
                null,
                null,
                null,
                facilities,
                rentalEquipments,
                10,
                autoSiteCount,
                0,
                caravanSiteCount,
                trailerAllowed,
                caravanAllowed,
                0,
                0,
                0,
                insured,
                fireSafetyCount,
                operating,
                closedFrom,
                closedTo,
                operationSeasons
        );
    }
}