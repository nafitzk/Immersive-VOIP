local ivui = require("ui")

-- Global config for setup
immersiveVoipConfigLua = {
    version = "0.1a"
}

-- A table like this should is added and updated from java, if the java side of the mod is correctly installed
-- immersiveVoipConfigJava {
--  version,
--  status
-- }

local player

-- runs when you first load into a game / server
local function onGameStart()
    print("[IV] Hello from Lua! :)")

    -- init player for later
    player = getSpecificPlayer(0)
    --print(player:getUsername())

    local status = false

    -- if we get no java side config from the java implementation, that means it is not installed
    -- or installed incorrectly and must be installed
    if immersiveVoipConfigJava == nil then
        print("Java config table is nil. Java side is not installed correctly")
        ivui.IvUpdateWindow:getInstance():forceVisible();

    -- if we do have a version and it is not the same as the Lua version, the java implementation must be
    -- updated manually
    elseif immersiveVoipConfigJava.version ~= immersiveVoipConfigLua.version then
        print("Version mismatch! Reinstall the java side")
        ivui.IvUpdateWindow:getInstance():forceVisible();

    -- if mod is installed and version strings check out, we do a final check to see if the java side of the mod
    -- configured properly. If not, display a window that something is wrong
    elseif not immersiveVoipConfigJava.status then
        print("Java side of the mod failed to init. Contact the creator and tell him he stinks :^)")
        ivui.IvUpdateWindow:getInstance():forceVisible();

    -- if we are here, then it looks like we are good to go
    else
        status = true
    end

    -- call the handshake function and pass on whether this mod should be enabled
    immersiveVoipHandshake(status)
end

-- NOTE TO THE CURIOUS:
-- the rest of these Lua functions are not used. My original plan was to have custom Lua events and callbacks, BUT
-- Zomboid's Lua scripting and threading architecture is such that all Lua function call stack frames are seemingly designed to be pushed and popped from a single thread with some global variables
-- This led to unpredictable behavior since I.V. is working off of the audio thread and not the main game thread. multithreading woes!
-- I will resolve this someday, but not today :)
-- for now, all of this logic has been implemented in IVEvent.java

local function getValidRadio()
    -- check to see if there is an active radio in the player's getInventory
    -- if there is, play the transmit beep sound
    -- there is probably a better way to do this, but given my limited exposure to zomboid's Lua integration, this will suffice for now
    local items = player:getInventory():getItems();
    for i=0, items:size() -1 do
        local item = items:get(i)
        if instanceof(item, "Radio") then
            local deviceData = item:getDeviceData()
            -- we will only play the beep on portable radios that are on and ready
            if deviceData:getIsTurnedOn() and deviceData:getIsPortable() then
                -- and those devices need to be unmuted both speaker and mic
                if not deviceData:getMicIsMuted() and deviceData:getDeviceVolume() > 0 then
                    return item
                end
            end
        end
    end
    return nil
end

local function transmitBegin()
    print("[IV] Begin voice transmit!")

    local radio = getValidRadio()
    if radio then
        local vol = radio:getDeviceData():getDeviceVolume()
        local id = player:getEmitter():playSound("transmission_start")
        player:getEmitter():setVolume(id, vol)
    end
end

local function transmitEnd()
    print("[IV] End voice transmit!")

    -- check to see if there is an active radio in the player's getInventory
    -- if there is, play the end transmit click
    local radio = getValidRadio()
    if radio then
        local vol = radio:getDeviceData():getDeviceVolume()
        local id = player:getEmitter():playSound("transmission_end")
        player:getEmitter():setVolume(id, vol)
    end
end

local function receiveBegin(username, source)

end

local function receiveEnd(username)

end

local function onInit()
    print("[IV] Init complete (from LUA!)")
end

Events.OnGameStart.Add(onGameStart)
--Events.ivOnInit.Add(onInit)
--Events.ivOnTransmitVoiceBegin.Add(transmitBegin)
--Events.ivOnTransmitVoiceEnd.Add(transmitEnd)