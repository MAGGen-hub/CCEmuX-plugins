if ccemux_open_with_size_temp and ccemux then
    for k,v in pairs(ccemux_open_with_size_temp)do
        _G.ccemux[k]=v
    end
    _G.ccemux_open_with_size_temp=nil
end
