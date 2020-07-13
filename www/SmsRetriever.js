var exec = require('cordova/exec');

var PLUGIN_NAME = "SmsRetriever";

var SmsRetriever = function() {};

SmsRetriever.prototype._events = {};

SmsRetriever.prototype.start = function(onSuccess, onError) {
  Cordova.exec(onSuccess, onError, PLUGIN_NAME, "start", []);
  return this;
};

SmsRetriever.prototype.stop = function(onSuccess, onError) {
  Cordova.exec(onSuccess, onError, PLUGIN_NAME, "stop", []);
  return this;
};

/**
 * Attaches an event handler to an event.
 *
 * The event handler must be a function that will be called when event is
 * triggered.
 *
 * The event handler must be defined with the following signature,
 *
 * ```js
 * function (event) {
 * }
 * ```
 *
 * where `event` is an [[Event]] object which includes parameters associated with the event.
 *
 * @param {string} name The event name
 * @param {componentEvent} handler The event handler
 * @return {this}
 * @see off()
 */
SmsRetriever.prototype.on = function(name, handler, data) {
  var _that = this;
  if (this._events[name] === undefined) {
    this._events[name] = [];
  }
  this._events[name].push([handler, data]);

  Cordova.exec(function (response) {
    return _that.trigger(response.name, response.data);
  }, function () {
    console.log('Error registering event');
  }, PLUGIN_NAME, "on", [name]);

  return this;
};

/**
 * Detaches an existing event handler from this component.
 * This method is the opposite of [[on()]].
 * @param {string} name The event name
 * @param {componentEvent} handler The event handler. If it is null, all
 * handlers attached to the named event will be removed.
 * @return {this}
 * @see on()
 */
SmsRetriever.prototype.off = function(name, handler) {
  var aux = [];
  var event;
  var i;
  if (this._events[name] !== undefined) {
    if (handler !== undefined) {
      for (i = 0; i < this._events[name].length; ++i) {
        event = this._events[name][i];
        if (event[0] !== handler) {
          aux.push(event);
        }
      }
    }
    this._events[name] = aux;
  }

  return this;
};

/**
 * Triggers an event.
 * This method represents the happening of an event. It invokes
 * all attached handlers for the event including class-level handlers.
 * @param {string} name The event name
 * @param {Event} payload The event payload. If not set, a default [[Event]] object will be created.
 * @return {this}
 */
SmsRetriever.prototype.trigger = function(name, payload) {
  return this.triggerOnEvent(name, payload);
};

/**
 * Triggers only "on" events.
 * @param {string} name The event name
 * @param {Event} payload The event payload. If not set, a default [[Event]] object will be created.
 * @return {this}
 */
SmsRetriever.prototype.triggerOnEvent = function(name, payload) {
  var i;
  var event;
  if (this._events[name] !== undefined) {
    for (i = 0; i < this._events[name].length; ++i) {
      event = this._events[name][i];
      event[0](payload);
    }
  }
  return this;
};

module.exports = new SmsRetriever();
