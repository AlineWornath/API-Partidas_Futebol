package com.neocamp.soccer_matches.testUtils;

import com.neocamp.soccer_matches.dto.state.StateResponseDto;
import com.neocamp.soccer_matches.entity.StateEntity;
import com.neocamp.soccer_matches.enums.StateCodeEnum;

public class StateMockUtils {
    public static StateEntity rs() {
        return new StateEntity("Rio Grande do Sul", StateCodeEnum.RS);
    }

    public static StateEntity sp() {
        return new StateEntity("São Paulo", StateCodeEnum.SP);
    }

    public static StateEntity rj() {
        return new StateEntity("Rio de Janeiro", StateCodeEnum.RJ);
    }

    public static StateResponseDto rjDto() {
        return new StateResponseDto(3L,"Rio de Janeiro", "RJ");
    }
}
