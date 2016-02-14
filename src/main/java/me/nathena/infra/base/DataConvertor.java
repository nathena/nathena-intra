/**
 * 
 */
package me.nathena.infra.base;

/**
 * @author GaoWx
 *
 */
public interface DataConvertor<T1, T2> {
	public T1 convertFromPo(T2 t2);
	public T2 convertToPo(T1 t1);
}
