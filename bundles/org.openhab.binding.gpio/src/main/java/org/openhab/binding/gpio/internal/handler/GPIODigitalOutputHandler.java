/**
 * Copyright (c) 2010-2020 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.openhab.binding.gpio.internal.handler;

import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.openhab.binding.gpio.internal.configuration.GPIOOutputConfiguration;
import org.openhab.core.library.types.OnOffType;
import org.openhab.core.types.Command;
import org.openhab.core.types.RefreshType;
import org.openhab.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eu.xeli.jpigpio.GPIO;
import eu.xeli.jpigpio.JPigpio;
import eu.xeli.jpigpio.PigpioException;

/**
 * Thing Handler for digital GPIO outputs.
 *
 * @author Nils Bauer - Initial contribution
 */
@NonNullByDefault
public class GPIODigitalOutputHandler implements ChannelHandler {

    /** The logger. */
    private final Logger logger = LoggerFactory.getLogger(GPIODigitalOutputHandler.class);

    private final GPIOOutputConfiguration configuration;
    private final GPIO gpio;
    private final Consumer<State> updateStatus;

    public GPIODigitalOutputHandler(GPIOOutputConfiguration configuration, JPigpio jPigpio,
            Consumer<State> updateStatus) throws PigpioException {
        this.configuration = configuration;
        this.updateStatus = updateStatus;
        this.gpio = new GPIO(jPigpio, configuration.gpioId, 0);
    }

    @Override
    public void handleCommand(Command command) {
        if (command instanceof RefreshType) {
            try {
                updateStatus.accept(OnOffType.from(configuration.invert != gpio.getValue()));
            } catch (PigpioException e) {
                logger.warn("Unknown pigpio exception while handling Refresh", e);
            }
        }
        if (command instanceof OnOffType) {
            try {
                gpio.setValue(configuration.invert != (OnOffType.ON.equals(command)));
            } catch (PigpioException e) {
                logger.warn("An error occured while changing the gpio value: {}", e.getMessage());
            }
        }
    }
}
