#pragma once

namespace Enumerators {

    using ClientRequestCode = unsigned short;

    enum ClientRequest : ClientRequestCode {
        SHUTDOWN = 1 << 1
    };

}
