package com.seohamin.campon.global.infra.tourApi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.List;

public record NearbyApiResponseDto(
        Response response
) {

    public record Response(
            Header header,
            Body body
    ) { }

    public record Header(
            String resultCode,
            String resultMsg
    ) { }

    public record Body(
            Items items,
            Integer numOfRows,
            Integer pageNo,
            Integer totalCount
    ) { }

    public record Items(
            @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            List<Item> item
    ) { }

    public record Item(
            String contentId,
            String facltNm,
            String lineIntro,
            String intro,
            String allar,
            String insrncAt,
            String trsagntNo,
            String bizrno,
            String facltDivNm,
            String mangeDivNm,
            String mgcDiv,
            String manageSttus,
            String hvofBgnde,
            String hvofEnddle,
            String featureNm,
            String induty,
            String lctCl,
            String doNm,
            String sigunguNm,
            String zipcode,
            String addr1,
            String addr2,
            String mapX,
            String mapY,
            String direction,
            String tel,
            String homepage,
            String resveUrl,
            String resveCl,
            String manageNmpr,
            String gnrlSiteCo,
            String autoSiteCo,
            String glampSiteCo,
            String caravSiteCo,
            String indvdlCaravSiteCo,
            String sitedStnc,
            String siteMg1Width,
            String siteMg2Width,
            String siteMg3Width,
            String siteMg1Vrticl,
            String siteMg2Vrticl,
            String siteMg3Vrticl,
            String siteMg1Co,
            String siteMg2Co,
            String siteMg3Co,
            String siteBottomCl1,
            String siteBottomCl2,
            String siteBottomCl3,
            String siteBottomCl4,
            String siteBottomCl5,
            String tooltip,
            String glampInnerFclty,
            String caravInnerFclty,
            String prmisnDe,
            String operPdCl,
            String operDeCl,
            String trlerAcmpnyAt,
            String caravAcmpnyAt,
            String toiletCo,
            String swrmCo,
            String wtrplCo,
            String brazierCl,
            String sbrsCl,
            String sbrsEtc,
            String posblFcltyCl,
            String posblFcltyEtc,
            String clturEventAt,
            String clturEvent,
            String exprnProgrmAt,
            String exprnProgrm,
            String extshrCo,
            String frprvtWrppCo,
            String frprvtSandCo,
            String fireSensorCo,
            String themaEnvrnCl,
            String eqpmnLendCl,
            String animalCmgCl,
            String tourEraCl,
            String firstImageUrl,
            String createdtime,
            String modifiedtime
    ) { }
}