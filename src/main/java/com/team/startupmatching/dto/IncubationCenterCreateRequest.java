// IncubationCenterCreateRequest.java
package com.team.startupmatching.dto;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class IncubationCenterCreateRequest {
    private String sourceId;             // pbanc_sn(없으면 id) - 업서트 기준
    private String title;                // biz_pbanc_nm
    private String region;               // supt_regin
    private String supportField;         // supt_biz_clsfc
    private LocalDate receiptStartDate;  // pbanc_rcpt_bgng_dt (yyyyMMdd → LocalDate로 파싱해서 넣기)
    private LocalDate receiptEndDate;    // pbanc_rcpt_end_dt
    private Boolean recruiting;          // rcrt_prgs_yn (Y/N → true/false)
    private String applyUrl;             // biz_aply_url | online | detl | guide
}
