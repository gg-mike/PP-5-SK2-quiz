#pragma once

namespace Enumerators {

    using MessageTypeCode = short;

    enum MessageType : MessageTypeCode {
        ERROR = -1,
        ACCEPT = 1,
        BECOME_HOST = 2,
        BECOME_PLAYER = 3,
        ADD_QUESTIONS = 4,
        START_GAME = 5,
        END_GAME = 6,
        START_ROUND = 7,
        END_ROUND = 8
    };

}
