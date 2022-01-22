#pragma once


namespace Enumerators {

    using GameStateCode = unsigned short;

    enum GameState : GameStateCode {
        CREATED,
        OPENED,
        STARTED,
        R_STARTED,
        R_ENDED,
        ENDED,
        SHUT
    };

}
