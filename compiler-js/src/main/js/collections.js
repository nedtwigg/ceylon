function Sized(wat) {
    return wat;
}
initType(Sized, 'ceylon.language.Sized', Container);
Sized.$$.prototype.getEmpty = function() {
    return Boolean$(this.getSize().value === 0);
}
exports.Sized=Sized;

function Iterable(wat) {
    return wat;
}
initType(Iterable, 'ceylon.language.Iterable', Container);
Iterable.$$.prototype.getEmpty = function() {
    return Boolean$(getIterator().next() === $finished);
}
exports.Iterable=Iterable;

function Category(wat) {
    return wat;
}
initType(Category, 'ceylon.language.Category');
Category.$$.prototype.containsEvery = function(keys) {
    for (var i = 0; i < keys.value.length; i++) {
        if (this.contains(keys.value[i]) === $false) {
            return $false;
        }
    }
    return $true;
}
Category.$$.prototype.containsAny = function(keys) {
    for (var i = 0; i < keys.value.length; i++) {
        if (this.contains(keys.value[i]) === $true) {
            return $true;
        }
    }
    return $false;
}
exports.Category=Category;

function Iterator(wat) {
    return wat;
}
initType(Iterator, 'ceylon.language.Iterator');
exports.Iterator=Iterator;
function Collection(wat) {
    return wat;
}
initType(Collection, 'ceylon.language.Collection', Iterable, Sized, Category, Cloneable);
inheritProto(Collection, Sized, '$Sized$');
inheritProto(Collection, Category, '$Category$');
var Collection$proto = Collection.$$.prototype;
Collection$proto.contains = function(obj) {
    var iter = this.getIterator();
    var item;
    while ((item = iter.next()) !== $finished) {
        if (exists(item) === $true && item.equals(obj) === $true) {
            return $true;
        }
    }
    return $false;
}
Collection$proto.count = function(obj) {
    var iter = this.getIterator();
    var item;
    var count = 0;
    while ((item = iter.next()) !== $finished) {
        if (exists(item) === $true && item.equals(obj) === $true) {
            count++;
        }
    }
    return Integer(count);
}
exports.Collection=Collection;

function FixedSized(wat) {
    return wat;
}
initType(FixedSized, 'ceylon.language.FixedSized', Collection);
inheritProto(FixedSized, Collection, '$Collection$');
var FixedSized$proto = FixedSized.$$.prototype;
FixedSized$proto.getFirst = function() {
    var e = this.getIterator().next();
    return e === $finished ? null : e;
}
exports.FixedSized=FixedSized;

function Some(wat) {
    return wat;
}
initType(Some, 'ceylon.language.Some', FixedSized);
inheritProto(Some, FixedSized, '$FixedSized$');
//we can skip inheritProto because we override the only inherited method
var $Some = Some.$$;
$Some.prototype.getFirst = function() {
    var e = this.getIterator().next();
    if (e === $finished) throw Exception();
    return e;
}
$Some.prototype.getEmpty = function() { return $false; }
exports.Some=Some;

function None(wat) {
    return wat;
}
initType(None, 'ceylon.language.None', FixedSized);
inheritProto(Some, FixedSized, '$FixedSized$');
//we can skip inheritProto because we override the only inherited method
var None$proto = None.$$.prototype;
None$proto.getFirst = function() { return null; }
None$proto.getIterator = function() { return emptyIterator; }
None$proto.getSize = function() { return Integer(0); }
None$proto.getEmpty = function() { return $true; }
exports.None=None;

function Ranged(wat) {
    return wat;
}
initType(Ranged, 'ceylon.language.Ranged');
exports.Ranged=Ranged;

function List(wat) {
    return wat;
}
initType(List, 'ceylon.language.List', Collection, Correspondence, Ranged, Cloneable);
inheritProto(List, Collection, '$Collection$');
inheritProto(List, Correspondence, '$Correspondence$');
//TODO implement methods
exports.List=List;

function Map(wat) {
    return wat;
}
initType(Map, 'ceylon.language.Map', Collection, Correspondence, Cloneable);
inheritProto(Map, Collection, '$Collection$');
inheritProto(Map, Correspondence, '$Correspondence$');
//TODO implement methods
exports.Map=Map;

function Set(wat) {
    return wat;
}
initType(Set, 'ceylon.language.Set', Collection, Cloneable);
inheritProto(Set, Collection, '$Collection$');
//TODO implement methods
exports.Set=Set;

