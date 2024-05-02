require("ISUI/ISCollapsableWindow")

local IvUpdateWindow = ISCollapsableWindow:derive("IvUpdateWindow");

IvUpdateWindow.config = {
    marginTop = 10,
    marginBottom = 10,
    marginLeft = 10,
    lineHeight = 20,
}

function IvUpdateWindow:new()
    local o

    o = ISCollapsableWindow:new(100,100,600,100);
    setmetatable(o, self);
    self.__index = self;

    o:setTitle("Immersive VOIP")
    o:setResizable(false)
    o:setVisible(false)
    o.visibleOnStartup = false
    o.playerNum = playerNum

    o:initialise()
    return o
end

function IvUpdateWindow:createChildren()
    ISCollapsableWindow.createChildren(self);
    self.textY = self:titleBarHeight() + self.config.marginTop
end

function IvUpdateWindow:getInstance()
    if self.instance == nil then
        self.instance = IvUpdateWindow:new();
    end
    return self.instance
end

function IvUpdateWindow:forceVisible()
    if not self:isVisible() then
        self:setVisible(true);
        self:addToUIManager();
    end
end

function IvUpdateWindow:prerender()
    ISCollapsableWindow.prerender(self)
    self:setHeight(self.textY * 3)
end

function IvUpdateWindow:render()
    ISCollapsableWindow.render(self)

    local left = self.config.marginLeft
    local y = self.textY

    self:drawText("Welcome to Immersive VOIP! :) :) :)", left, y, 1, 1, 1, 1)
end

local ui = {
    IvUpdateWindow = IvUpdateWindow
}
ui.IvUpdateWindow:getInstance()
return ui

