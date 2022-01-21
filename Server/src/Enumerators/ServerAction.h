#pragma once

namespace Enumerators {

    using ServerActionCode = unsigned short;

    enum ServerAction : ServerActionCode {
        CLOSE_CONN = 1 << 1
    };

}
