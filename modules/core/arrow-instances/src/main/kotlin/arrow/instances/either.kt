package arrow.instances

import arrow.Kind
import arrow.core.*
import arrow.instance
import arrow.typeclasses.*

@instance(Either::class)
interface EitherFunctorInstance<L> : Functor<EitherKindPartial<L>> {
    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)
}

@instance(Either::class)
interface EitherApplicativeInstance<L> : EitherFunctorInstance<L>, Applicative<EitherKindPartial<L>> {

    override fun <A> pure(a: A): Either<L, A> = Right(a)

    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)
}

@instance(Either::class)
interface EitherMonadInstance<L> : EitherApplicativeInstance<L>, Monad<EitherKindPartial<L>> {

    override fun <A, B> map(fa: EitherKind<L, A>, f: (A) -> B): Either<L, B> = fa.ev().map(f)

    override fun <A, B> ap(fa: EitherKind<L, A>, ff: EitherKind<L, (A) -> B>): Either<L, B> =
            fa.ev().ap(ff)

    override fun <A, B> flatMap(fa: EitherKind<L, A>, f: (A) -> EitherKind<L, B>): Either<L, B> = fa.ev().flatMap { f(it).ev() }

    override fun <A, B> tailRecM(a: A, f: (A) -> Kind<EitherKindPartial<L>, Either<A, B>>): Either<L, B> =
            Either.tailRecM(a, f)
}

@instance(Either::class)
interface EitherApplicativeErrorInstance<L> : EitherApplicativeInstance<L>, ApplicativeError<EitherKindPartial<L>, L> {

    override fun <A> raiseError(e: L): Either<L, A> = Left(e)

    override fun <A> handleErrorWith(fa: Kind<EitherKindPartial<L>, A>, f: (L) -> Kind<EitherKindPartial<L>, A>): Either<L, A> {
        val fea = fa.ev()
        return when (fea) {
            is Either.Left -> f(fea.a).ev()
            is Either.Right -> fea
        }
    }
}

@instance(Either::class)
interface EitherMonadErrorInstance<L> : EitherApplicativeErrorInstance<L>, EitherMonadInstance<L>, MonadError<EitherKindPartial<L>, L>

@instance(Either::class)
interface EitherFoldableInstance<L> : Foldable<EitherKindPartial<L>> {

    override fun <A, B> foldLeft(fa: Kind<EitherKindPartial<L>, A>, b: B, f: (B, A) -> B): B =
            fa.ev().foldLeft(b, f)

    override fun <A, B> foldRight(fa: Kind<EitherKindPartial<L>, A>, lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
            fa.ev().foldRight(lb, f)
}

fun <G, A, B, C> Either<A, B>.traverse(f: (B) -> Kind<G, C>, GA: Applicative<G>): Kind<G, Either<A, C>> =
        this.ev().fold({ GA.pure(Either.Left(it)) }, { GA.map(f(it), { Either.Right(it) }) })

@instance(Either::class)
interface EitherTraverseInstance<L> : EitherFoldableInstance<L>, Traverse<EitherKindPartial<L>> {

    override fun <G, A, B> traverse(fa: Kind<EitherKindPartial<L>, A>, f: (A) -> Kind<G, B>, GA: Applicative<G>): Kind<G, Kind<EitherKindPartial<L>, B>> =
            fa.ev().traverse(f, GA)
}

@instance(Either::class)
interface EitherSemigroupKInstance<L> : SemigroupK<EitherKindPartial<L>> {

    override fun <A> combineK(x: EitherKind<L, A>, y: EitherKind<L, A>): Either<L, A> =
            x.ev().combineK(y)
}

@instance(Either::class)
interface EitherEqInstance<L, R> : Eq<Either<L, R>> {

    fun EQL(): Eq<L>

    fun EQR(): Eq<R>

    override fun eqv(a: Either<L, R>, b: Either<L, R>): Boolean = when (a) {
        is Either.Left -> when (b) {
            is Either.Left -> EQL().eqv(a.a, b.a)
            is Either.Right -> false
        }
        is Either.Right -> when (b) {
            is Either.Left -> false
            is Either.Right -> EQR().eqv(a.b, b.b)
        }
    }

}
