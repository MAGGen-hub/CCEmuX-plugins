if ccemux_open_with_size_temp and ccemux then
    for k,v in pairs(ccemux_open_with_size_temp)do
        _G.ccemux[k]=v
    end
    ccemux.openEmuWithComputerSize=function(id)return ccemux.openEmuWithSize(id,51,19) end
    ccemux.openEmuWithPocketComputerSize=function(id)return ccemux.openEmuWithSize(id,26,20) end
    ccemux.openEmuWithDefaultMonitorSize=function(id)return ccemux.openEmuWithSize(id,8,6) end
    ccemux.openEmuWithTurtleSize=function(id)return ccemux.openEmuWithSize(id,39,13) end
    _G.ccemux_open_with_size_temp=nil
end
