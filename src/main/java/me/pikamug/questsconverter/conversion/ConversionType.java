/*
 * Copyright (c) 2020 PikaMug. All rights reserved.
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package me.pikamug.questsconverter.conversion;

import java.util.List;

import com.google.common.collect.ImmutableList;

public enum ConversionType {
    
    PLAYERDATA("PlayerData", "Playerdata", "playerdata");
    
    private final String name;

    private final List<String> identifiers;

    ConversionType(final String name, final String... identifiers) {
        this.name = name;
        this.identifiers = ImmutableList.copyOf(identifiers);
    }

    public static ConversionType parse(final String name, final ConversionType def) {
        for (final ConversionType t : values()) {
            for (final String id : t.getIdentifiers()) {
                if (id.equalsIgnoreCase(name)) {
                    return t;
                }
            }
        }
        return def;
    }

    public String getName() {
        return name;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }
}
