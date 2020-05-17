/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.admin;

import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.admin.exception.AdminException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandNotFoundException;
import fr.quatrevieux.araknemu.game.admin.exception.CommandPermissionsException;
import fr.quatrevieux.araknemu.game.admin.exception.ContextNotFoundException;
import fr.quatrevieux.araknemu.network.game.out.basic.admin.CommandResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AdminUserTest extends GameBaseCase {
    private AdminUser user;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        user = new AdminUser(
            container.get(AdminService.class),
            gamePlayer()
        );

        gamePlayer().account().grant(Permission.ACCESS);
    }

    @Test
    void isGranted() throws SQLException, ContainerException {
        assertFalse(user.isGranted(EnumSet.of(Permission.MANAGE_ACCOUNT)));

        gamePlayer().account().grant(Permission.MANAGE_ACCOUNT);

        assertTrue(user.isGranted(EnumSet.of(Permission.MANAGE_ACCOUNT)));
    }

    @Test
    void logNoArguments() {
        user.log(LogType.SUCCESS, "My message");

        requestStack.assertLast(
            new CommandResult(LogType.SUCCESS, "My message")
        );
    }

    @Test
    void logOneArgument() {
        user.log(LogType.SUCCESS, "Hello {} !", "John");

        requestStack.assertLast(
            new CommandResult(LogType.SUCCESS, "Hello John !")
        );
    }

    @Test
    void logManyArguments() {
        user.log(LogType.SUCCESS, "Hello {}, My name is {} and I'm {} Y-O !", "John", "Mark", 26);

        requestStack.assertLast(
            new CommandResult(LogType.SUCCESS, "Hello John, My name is Mark and I'm 26 Y-O !")
        );
    }

    @Test
    void info() {
        user.info("Hello {} !", "John");

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "Hello John !")
        );
    }

    @Test
    void success() {
        user.success("Hello {} !", "John");

        requestStack.assertLast(
            new CommandResult(LogType.SUCCESS, "Hello John !")
        );
    }

    @Test
    void error() {
        user.error("Hello {} !", "John");

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "Hello John !")
        );
    }

    @Test
    void executeNoPermissions() {
        assertThrows(CommandPermissionsException.class, () -> user.execute("info"));
    }

    @Test
    void executeCommandNotFound() {
        assertThrows(CommandNotFoundException.class, () -> user.execute("not_found_command"));
    }

    @Test
    void executeContextNotFound() {
        assertThrows(ContextNotFoundException.class, () -> user.execute("$not_found info"));
    }

    @Test
    void executeSuccess() throws AdminException {
        user.execute("echo Hello World !");

        requestStack.assertLast(
            new CommandResult(LogType.DEFAULT, "Hello World !")
        );
    }

    @Test
    void errorException() {
        user.error(new CommandNotFoundException("test"));

        requestStack.assertLast(
            new CommandResult(LogType.ERROR, "Command 'test' is not found")
        );
    }

    @Test
    void player() throws SQLException, ContainerException {
        assertSame(gamePlayer(), user.player());
    }

    @Test
    void send() {
        user.send("test");

        requestStack.assertLast("test");
    }
}
