package com.taai.app.dto.remote;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FirstDSStockResponseDTO {
    private List<String> arrDetailStock;
}
